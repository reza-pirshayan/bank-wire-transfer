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
import jakarta.transaction.Transactional;

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
		super();
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

		if (achTransferOrderId == null) {
			throw new IllegalArgumentException(
					"AchTransferOrderAggregateRepositoryImpl.findById cannot accept null parameter");
		}

		AchTransferOrderEntity achTransferOrderEntity = achTransferOrderEntityRepository
				.findByIdOptional(achTransferOrderId.getId())
				.orElseThrow(() -> new AchTransferOrderNotFoundException(achTransferOrderId));

		achTransferOrderEntity.setFirstSignerCandidateEntities(firstSignerCandidateEntityRepository
				.findByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId()));
		achTransferOrderEntity.setSecondSignerCandidateEntities(secondSignerCandidateEntityRepository
				.findByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId()));

		switch (achTransferOrderEntity.getStatus()) {
		case 0 -> {
			return PendingFirstSignatureAchTransferOrderMapper.toModel(achTransferOrderEntity);
		}
		case 1 -> {
			FirstSignatureEntity firstSignatureEntity = firstSignatureEntityRepository
					.findByIdOptional(achTransferOrderId.getId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 1));
			return PendingSecondSignatureAchTransferOrderMapper.toModel(firstSignatureEntity);
		}
		case 2 -> {
			SecondSignatureEntity secondSignatureEntity = secondSignatureEntityRepository
					.findByIdOptional(achTransferOrderId.getId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 2));
			return PendingSendAchTransferOrderMapper.toModel(secondSignatureEntity);
		}
		default -> throw new IllegalStateException(String.format(
				"Unhandled AchTransferOrderAggregateRepository.findById for ACH transfer order with ID [ %s ] and status [ %d ]",
				achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
		}
	}

	@Transactional
	@Override
	public void create(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {

		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException(
					"AchTransferOrderAggregateRootRepositoryImpl.create cannot accept null parameter");
		}

		if (!achTransferOrderAggregateRoot.isPendingFirstSignature()) {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and state [ %s ] cannot be created",
							achTransferOrderAggregateRoot.getAchTransferOrderId().getId(),
							achTransferOrderAggregateRoot.getStatusString()));
		}

		AchTransferOrderEntity achTransferOrderEntity = PendingFirstSignatureAchTransferOrderMapper
				.toEntity(achTransferOrderAggregateRoot);
		achTransferOrderEntityRepository.persist(achTransferOrderEntity);

		List<FirstSignerCandidateEntity> firstCandidates = achTransferOrderAggregateRoot.getFirstSignerCandidateIds()
				.stream().map(ruleId -> new FirstSignerCandidateEntity(achTransferOrderEntity, ruleId.getId()))
				.toList();
		firstSignerCandidateEntityRepository.persist(firstCandidates);

		List<SecondSignerCandidateEntity> secondCandidates = achTransferOrderAggregateRoot.getSecondSignerCandidateIds()
				.stream().map(ruleId -> new SecondSignerCandidateEntity(achTransferOrderEntity, ruleId.getId()))
				.toList();
		secondSignerCandidateEntityRepository.persist(secondCandidates);

	}

	@Transactional
	@Override
	public void update(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {
		AchTransferOrderId achTransferOrderId = achTransferOrderAggregateRoot.getAchTransferOrderId();

		AchTransferOrderEntity achTransferOrderEntity = achTransferOrderEntityRepository
				.findByIdOptional(achTransferOrderId.getId())
				.orElseThrow(() -> new AchTransferOrderNotFoundException(achTransferOrderId));

		if (achTransferOrderAggregateRoot.isPendingSecondSignature()) {
			achTransferOrderEntity.setStatus(1);

			FirstSignatureEntity firstSignatureEntity = PendingSecondSignatureAchTransferOrderMapper
					.toEntity(achTransferOrderAggregateRoot, achTransferOrderEntity);
			firstSignatureEntityRepository.persist(firstSignatureEntity);

			firstSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());
			secondSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());

			List<SecondSignerCandidateEntity> secondCandidates = achTransferOrderAggregateRoot
					.getSecondSignerCandidateIds().stream()
					.map(ruleId -> new SecondSignerCandidateEntity(achTransferOrderEntity, ruleId.getId())).toList();
			secondSignerCandidateEntityRepository.persist(secondCandidates);
			return;
		}

		if (achTransferOrderAggregateRoot.isPendingSend()) {
			achTransferOrderEntity.setStatus(2);

			FirstSignatureEntity firstSignatureEntity = firstSignatureEntityRepository
					.findByIdOptional(achTransferOrderEntity.getOrderId())
					.orElseThrow(() -> new InconsistentAchTransferOrderException(achTransferOrderId, 2));

			SecondSignatureEntity secondSignatureEntity = PendingSendAchTransferOrderMapper
					.toEntity(achTransferOrderAggregateRoot, firstSignatureEntity);
			secondSignatureEntityRepository.persist(secondSignatureEntity);

			secondSignerCandidateEntityRepository.deleteByAchTransferOrderEntityId(achTransferOrderEntity.getOrderId());
			return;
		}

		throw new IllegalStateException(String.format(
				"Unhandled AchTransferOrderRepository.update for ACH transfer order with ID [ %s ] and status [ %d ]",
				achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
	}

	@Override
	@Transactional
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
