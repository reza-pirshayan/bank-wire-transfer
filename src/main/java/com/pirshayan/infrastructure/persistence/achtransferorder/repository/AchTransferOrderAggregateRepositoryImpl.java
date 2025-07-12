package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import java.util.List;
import java.util.stream.Collectors;

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
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AchTransferOrderAggregateRepositoryImpl implements AchTransferOrderAggregateRepository {

	private final AchTransferOrderEntityRepository achTransferOrderEntityRepository;
	private final FirstSignatureEntityRepository firstSignatureEntityRepository;
	private final SecondSignatureEntityRepository secondSignatureEntityRepository;
	private final FirstSignerCandidateEntityRepository firstSignerCandidateEntityRepository;
	private final SecondSignerCandidateEntityRepository secondSignerCandidateEntityRepository;

	public AchTransferOrderAggregateRepositoryImpl(AchTransferOrderEntityRepository achTransferOrderEntityRepository,
			FirstSignatureEntityRepository firstSignatureEntityRepository,
			SecondSignatureEntityRepository secondSignatureEntityRepository,
			FirstSignerCandidateEntityRepository firstSignerCandidateEntityRepository,
			SecondSignerCandidateEntityRepository secondSignerCandidateEntityRepository) {
		this.achTransferOrderEntityRepository = achTransferOrderEntityRepository;
		this.firstSignatureEntityRepository = firstSignatureEntityRepository;
		this.secondSignatureEntityRepository = secondSignatureEntityRepository;
		this.firstSignerCandidateEntityRepository = firstSignerCandidateEntityRepository;
		this.secondSignerCandidateEntityRepository = secondSignerCandidateEntityRepository;
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
				.collect(Collectors.toList());
		firstSignerCandidateEntityRepository.persist(firstCandidates);

		List<SecondSignerCandidateEntity> secondCandidates = achTransferOrderAggregateRoot.getSecondSignerCandidateIds()
				.stream().map(ruleId -> new SecondSignerCandidateEntity(achTransferOrderEntity, ruleId.getId()))
				.collect(Collectors.toList());
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
					.map(ruleId -> new SecondSignerCandidateEntity(achTransferOrderEntity, ruleId.getId()))
					.collect(Collectors.toList());
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

}
