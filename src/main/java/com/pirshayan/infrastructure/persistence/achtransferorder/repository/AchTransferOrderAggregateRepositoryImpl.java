package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import java.util.List;
import java.util.Optional;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignerCandidateEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignerCandidateEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.mapper.PendingFirstSignatureAchTransferOrderMapper;
import com.pirshayan.infrastructure.persistence.achtransferorder.mapper.PendingSecondSignatureAchTransferOrderMapper;
import com.pirshayan.infrastructure.persistence.achtransferorder.mapper.PendingSendAchTransferOrderMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class AchTransferOrderAggregateRepositoryImpl implements AchTransferOrderAggregateRepository {

	private final AchTransferOrderEntityRepository achTransferOrderEntityRepository;
	private final FirstSignatureEntityRepository firstSignatureEntityRepository;
	private final SecondSignatureEntityRepository secondSignatureEntityRepository;
	private final FirstSignerCandidateEntityRepository firstSignerCandidateEntityRepository;
	private final SecondSignerCandidateEntityRepository secondSignerCandidateEntityRepository;
	private final EntityManager em;

	public AchTransferOrderAggregateRepositoryImpl(AchTransferOrderEntityRepository achTransferOrderEntityRepository,
			FirstSignatureEntityRepository firstSignatureEntityRepository,
			SecondSignatureEntityRepository secondSignatureEntityRepository,
			FirstSignerCandidateEntityRepository firstSignerCandidateEntityRepository,
			SecondSignerCandidateEntityRepository secondSignerCandidateEntityRepository, EntityManager em) {
		this.achTransferOrderEntityRepository = achTransferOrderEntityRepository;
		this.firstSignatureEntityRepository = firstSignatureEntityRepository;
		this.secondSignatureEntityRepository = secondSignatureEntityRepository;
		this.firstSignerCandidateEntityRepository = firstSignerCandidateEntityRepository;
		this.secondSignerCandidateEntityRepository = secondSignerCandidateEntityRepository;
		this.em = em;
	}

	public void clearPersistenceContext() {
		em.clear(); // Ensures a fresh fetch from DB
	}

	@Override
	public AchTransferOrderAggregateRoot findById(AchTransferOrderId achTransferOrderId) {

		// Defensive check: ensure the input parameter is not null
		if (achTransferOrderId == null) {
			throw new IllegalArgumentException(
				"AchTransferOrderAggregateRepositoryImpl.findById cannot accept null parameter");
		}

		// Retrieve the main ACH transfer order entity from the database
		AchTransferOrderEntity achTransferOrderEntity = achTransferOrderEntityRepository
			.findByIdOptional(achTransferOrderId.getId())
			.orElseThrow(() -> new AchTransferOrderNotFoundException(achTransferOrderId));

		// Load associated signer candidate lists (first and second) into the entity
		achTransferOrderEntity.setFirstSignerCandidateEntities(
			firstSignerCandidateEntityRepository.findByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId())
		);
		achTransferOrderEntity.setSecondSignerCandidateEntities(
			secondSignerCandidateEntityRepository.findByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId())
		);

		// Convert to domain model based on current status
		switch (achTransferOrderEntity.getStatus()) {

			// 0 = Pending first signature
			case 0 -> {
				return PendingFirstSignatureAchTransferOrderMapper.toModel(achTransferOrderEntity);
			}

			// 1 = Pending second signature
			case 1 -> {
				FirstSignatureEntity firstSignatureEntity = firstSignatureEntityRepository
					.findByIdOptional(achTransferOrderId.getId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 1));
				return PendingSecondSignatureAchTransferOrderMapper.toModel(firstSignatureEntity);
			}

			// 2 = Pending send
			case 2 -> {
				SecondSignatureEntity secondSignatureEntity = secondSignatureEntityRepository
					.findByIdOptional(achTransferOrderId.getId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 2));
				return PendingSendAchTransferOrderMapper.toModel(secondSignatureEntity);
			}

			// Handle unexpected status codes
			default -> throw new IllegalStateException(String.format(
				"Unhandled AchTransferOrderAggregateRepository.findById for ACH transfer order with ID [ %s ] and status [ %d ]",
				achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
		}
	}

	@Override
	public void create(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {

		// Defensive check: ensure the aggregate root is not null
		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException(
					"AchTransferOrderAggregateRootRepositoryImpl.create cannot accept null parameter");
		}

		// Business rule: new ACH transfer orders must start in the
		// PENDING_FIRST_SIGNATURE state
		if (!achTransferOrderAggregateRoot.isPendingFirstSignature()) {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and state [ %s ] cannot be created",
							achTransferOrderAggregateRoot.getAchTransferOrderId().getId(),
							achTransferOrderAggregateRoot.getStatusString()));
		}

		// Convert the domain model to a JPA entity and persist it
		AchTransferOrderEntity achTransferOrderEntity = PendingFirstSignatureAchTransferOrderMapper
				.toEntity(achTransferOrderAggregateRoot);
		achTransferOrderEntityRepository.persist(achTransferOrderEntity);

		// Persist the list of valid first signer candidates associated with this
		// transfer
		List<FirstSignerCandidateEntity> firstCandidates = achTransferOrderAggregateRoot
				.getFirstSignerCandidateRuleIds().stream()
				.map(ruleId -> new FirstSignerCandidateEntity(achTransferOrderEntity, ruleId.getId())).toList();
		firstSignerCandidateEntityRepository.persist(firstCandidates);

		// Persist the list of valid second signer candidates associated with this
		// transfer
		List<SecondSignerCandidateEntity> secondCandidates = achTransferOrderAggregateRoot
				.getSecondSignerCandidateRuleIds().stream()
				.map(ruleId -> new SecondSignerCandidateEntity(achTransferOrderEntity, ruleId.getId())).toList();
		secondSignerCandidateEntityRepository.persist(secondCandidates);
	}

	@Override
	public void update(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {
		AchTransferOrderId achTransferOrderId = achTransferOrderAggregateRoot.getAchTransferOrderId();

		// Retrieve the persisted ACH transfer order entity from the database.
		AchTransferOrderEntity achTransferOrderEntity = achTransferOrderEntityRepository
				.findByIdOptional(achTransferOrderId.getId())
				.orElseThrow(() -> new AchTransferOrderNotFoundException(achTransferOrderId));

		// Case 1: Update the transfer order after the first signature is collected
		if (achTransferOrderAggregateRoot.isPendingSecondSignature()) {
			achTransferOrderEntity.setStatus(1); // Update status to "Pending Second Signature"

			// Persist the first signature to the database
			FirstSignatureEntity firstSignatureEntity = PendingSecondSignatureAchTransferOrderMapper
					.toEntity(achTransferOrderAggregateRoot, achTransferOrderEntity);
			firstSignatureEntityRepository.persist(firstSignatureEntity);

			// Remove existing signer candidate records (both first and second)
			firstSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());
			secondSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());

			// Persist updated second signer candidate list
			List<SecondSignerCandidateEntity> secondCandidates = achTransferOrderAggregateRoot
					.getSecondSignerCandidateRuleIds().stream()
					.map(ruleId -> new SecondSignerCandidateEntity(achTransferOrderEntity, ruleId.getId())).toList();
			secondSignerCandidateEntityRepository.persist(secondCandidates);

			return;
		}

		// Case 2: Update the transfer order after the second signature is collected
		if (achTransferOrderAggregateRoot.isPendingSend()) {
			achTransferOrderEntity.setStatus(2); // Update status to "Ready to Send"

			// Load the previously saved first signature
			FirstSignatureEntity firstSignatureEntity = firstSignatureEntityRepository
					.findByIdOptional(achTransferOrderEntity.getOrderId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 2));

			// Create and persist the second signature
			SecondSignatureEntity secondSignatureEntity = PendingSendAchTransferOrderMapper
					.toEntity(achTransferOrderAggregateRoot, firstSignatureEntity);
			secondSignatureEntityRepository.persist(secondSignatureEntity);

			// Remove all second signer candidates (approval is completed)
			secondSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());

			return;
		}

		// Fallback for any unrecognized or inconsistent status
		throw new IllegalStateException(String.format(
				"Unhandled AchTransferOrderRepository.update for ACH transfer order with ID [ %s ] and status [ %d ]",
				achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
	}

	@Override
	public void deleteById(AchTransferOrderId achTransferOrderId) {
		if (achTransferOrderId == null) {
			throw new IllegalArgumentException(
					"AchTransferOrderAggregateRepository.deleteById cannot accept null as parameter");
		}

		Optional<AchTransferOrderEntity> achTransferOrderEntityOptional = achTransferOrderEntityRepository
				.findByIdOptional(achTransferOrderId.getId());

		if (achTransferOrderEntityOptional.isEmpty()) {
			return;
		}

		if (achTransferOrderEntityOptional.get().getStatus() == 0) {

			secondSignerCandidateEntityRepository.delete(
					"DELETE FROM SecondSignerCandidateEntity s WHERE s.achTransferOrderEntity.orderId = ?1",
					achTransferOrderId.getId());

			firstSignerCandidateEntityRepository.delete(
					"DELETE FROM FirstSignerCandidateEntity f WHERE f.achTransferOrderEntity.orderId = ?1",
					achTransferOrderId.getId());

			achTransferOrderEntityRepository.delete("DELETE FROM AchTransferOrderEntity a WHERE a.orderId = ?1",
					achTransferOrderId.getId());

			return;
		}

		if (achTransferOrderEntityOptional.get().getStatus() == 1) {

			firstSignatureEntityRepository.delete("DELETE FROM FirstSignatureEntity f WHERE f.id = ?1",
					achTransferOrderId.getId());

			secondSignerCandidateEntityRepository.delete(
					"DELETE FROM SecondSignerCandidateEntity s WHERE s.achTransferOrderEntity.orderId = ?1",
					achTransferOrderId.getId());

			achTransferOrderEntityRepository.delete("DELETE FROM AchTransferOrderEntity a WHERE a.orderId = ?1",
					achTransferOrderId.getId());

			return;
		}

		if (achTransferOrderEntityOptional.get().getStatus() == 2) {

			secondSignatureEntityRepository.delete("DELETE FROM SecondSignatureEntity s WHERE s.id = ?1",
					achTransferOrderId.getId());

			firstSignatureEntityRepository.delete("DELETE FROM FirstSignatureEntity f WHERE f.id = ?1",
					achTransferOrderId.getId());

			achTransferOrderEntityRepository.delete("DELETE FROM AchTransferOrderEntity a WHERE a.orderId = ?1",
					achTransferOrderId.getId());

		}
	}
}
