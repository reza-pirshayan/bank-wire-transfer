package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import java.util.List;
import java.util.Optional;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderPersistenceStatus;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignerCandidateEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignerCandidateEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.mapper.AchTransferOrderMapper;
import com.pirshayan.infrastructure.persistence.achtransferorder.mapper.AchTransferOrderMapperHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class AchTransferOrderAggregateRepositoryImpl implements AchTransferOrderAggregateRepository {

	private final AchTransferOrderEntityRepository achTransferOrderEntityRepository;
	private final FirstSignatureEntityRepository firstSignatureEntityRepository;
	private final SecondSignatureEntityRepository secondSignatureEntityRepository;
	private final FirstSignerCandidateEntityRepository firstSignerCandidateEntityRepository;
	private final SecondSignerCandidateEntityRepository secondSignerCandidateEntityRepository;
	private final AchTransferOrderMapperHandler achTransferOrderMapperHandler;
	private final EntityManager em;

	public AchTransferOrderAggregateRepositoryImpl(AchTransferOrderEntityRepository achTransferOrderEntityRepository,
			FirstSignatureEntityRepository firstSignatureEntityRepository,
			SecondSignatureEntityRepository secondSignatureEntityRepository,
			FirstSignerCandidateEntityRepository firstSignerCandidateEntityRepository,
			SecondSignerCandidateEntityRepository secondSignerCandidateEntityRepository,
			AchTransferOrderMapperHandler achTransferOrderMapperHandler, EntityManager em) {
		this.achTransferOrderEntityRepository = achTransferOrderEntityRepository;
		this.firstSignatureEntityRepository = firstSignatureEntityRepository;
		this.secondSignatureEntityRepository = secondSignatureEntityRepository;
		this.firstSignerCandidateEntityRepository = firstSignerCandidateEntityRepository;
		this.secondSignerCandidateEntityRepository = secondSignerCandidateEntityRepository;
		this.achTransferOrderMapperHandler = achTransferOrderMapperHandler;
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
		achTransferOrderEntity.setFirstSignerCandidateEntities(firstSignerCandidateEntityRepository
				.findByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId()));
		achTransferOrderEntity.setSecondSignerCandidateEntities(secondSignerCandidateEntityRepository
				.findByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId()));

		AchTransferOrderMapper achTransferOrderMapper = achTransferOrderMapperHandler.getMapper(achTransferOrderEntity);

		// Convert to domain model based on current status
		switch (achTransferOrderEntity.getStatus()) {

		case PENDING_FIRST_SIGNATURE -> {
			return achTransferOrderMapper.toModel(achTransferOrderEntity);
		}

		case PENDING_SECOND_SIGNATURE -> {
			FirstSignatureEntity firstSignatureEntity = firstSignatureEntityRepository
					.findByIdOptional(achTransferOrderId.getId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 1));
			return achTransferOrderMapper.toModel(firstSignatureEntity);
		}

		case PENDING_SEND -> {
			SecondSignatureEntity secondSignatureEntity = secondSignatureEntityRepository
					.findByIdOptional(achTransferOrderId.getId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 2));
			return achTransferOrderMapper.toModel(secondSignatureEntity);
		}

		// Handle unexpected status codes
		default -> throw new IllegalStateException(String.format(
				"Unhandled AchTransferOrderAggregateRepository.findById for ACH transfer order with ID [ %s ] and status [ %s ]",
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

		AchTransferOrderMapper achTransferOrderMapper = achTransferOrderMapperHandler
				.getMapper(achTransferOrderAggregateRoot);

		// Convert the domain model to a JPA entity and persist it
		AchTransferOrderEntity achTransferOrderEntity = (AchTransferOrderEntity) achTransferOrderMapper
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

		AchTransferOrderMapper achTransferOrderMapper = achTransferOrderMapperHandler
				.getMapper(achTransferOrderAggregateRoot);

		// Retrieve the persisted ACH transfer order entity from the database.
		AchTransferOrderEntity achTransferOrderEntity = achTransferOrderEntityRepository
				.findByIdOptional(achTransferOrderId.getId())
				.orElseThrow(() -> new AchTransferOrderNotFoundException(achTransferOrderId));

		// Case 1: Update the transfer order after the first signature is collected
		if (achTransferOrderAggregateRoot.isPendingSecondSignature()) {
			achTransferOrderEntity.setStatus(AchTransferOrderPersistenceStatus.PENDING_SECOND_SIGNATURE);

			// Persist the first signature to the database
			FirstSignatureEntity firstSignatureEntity = (FirstSignatureEntity) achTransferOrderMapper
					.toEntity(achTransferOrderAggregateRoot);
			firstSignatureEntity.setAchTransferOrderEntity(achTransferOrderEntity);
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
			achTransferOrderEntity.setStatus(AchTransferOrderPersistenceStatus.PENDING_SEND);

			// Load the previously saved first signature
			FirstSignatureEntity firstSignatureEntity = firstSignatureEntityRepository
					.findByIdOptional(achTransferOrderEntity.getOrderId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 2));

			// Create and persist the second signature
			SecondSignatureEntity secondSignatureEntity = (SecondSignatureEntity) achTransferOrderMapper
					.toEntity(achTransferOrderAggregateRoot);
			secondSignatureEntity.setFirstSignatureEntity(firstSignatureEntity);
			secondSignatureEntityRepository.persist(secondSignatureEntity);

			// Remove all second signer candidates (approval is completed)
			secondSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());
		}

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

		if (achTransferOrderEntityOptional.get().getStatus() == AchTransferOrderPersistenceStatus.PENDING_FIRST_SIGNATURE) {

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

		if (achTransferOrderEntityOptional.get().getStatus() == AchTransferOrderPersistenceStatus.PENDING_SECOND_SIGNATURE) {

			firstSignatureEntityRepository.delete("DELETE FROM FirstSignatureEntity f WHERE f.id = ?1",
					achTransferOrderId.getId());

			secondSignerCandidateEntityRepository.delete(
					"DELETE FROM SecondSignerCandidateEntity s WHERE s.achTransferOrderEntity.orderId = ?1",
					achTransferOrderId.getId());

			achTransferOrderEntityRepository.delete("DELETE FROM AchTransferOrderEntity a WHERE a.orderId = ?1",
					achTransferOrderId.getId());

			return;
		}

		if (achTransferOrderEntityOptional.get().getStatus() == AchTransferOrderPersistenceStatus.PENDING_SEND) {

			secondSignatureEntityRepository.delete("DELETE FROM SecondSignatureEntity s WHERE s.id = ?1",
					achTransferOrderId.getId());

			firstSignatureEntityRepository.delete("DELETE FROM FirstSignatureEntity f WHERE f.id = ?1",
					achTransferOrderId.getId());

			achTransferOrderEntityRepository.delete("DELETE FROM AchTransferOrderEntity a WHERE a.orderId = ?1",
					achTransferOrderId.getId());
			
			return;

		}
		
		throw new IllegalStateException("ACH transfer order with ID [ %s ] cannot be deleted");
	}
}
