package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;

public class PendingFirstSignatureAchTransferOrderMapper {

	public static AchTransferOrderAggregateRoot toModel(AchTransferOrderEntity achTransferOrderEntity) {
		if (achTransferOrderEntity.getStatus() != 0) {
			throw new IllegalStateException(String.format(
					"ACH transfer order EINTITY with ID [ %s ] and status [ %d ] cannot mapped to PendigFirstSignature Aggregate",
					achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
		}

		if (achTransferOrderEntity.getFirstSignerCandidateEntities().isEmpty()) {
			throw new IllegalStateException(String.format(
					"ACH transfer order entity with ID [ %s ] and status [ %s ] cannot have empty first signer candidate list",
					achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
		}

		if (achTransferOrderEntity.getSecondSignerCandidateEntities().isEmpty()) {
			throw new IllegalStateException(String.format(
					"ACH transfer order entity with ID [ %s ] and status [ %s ] cannot have empty second signer candidate list",
					achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
		}

		return AchTransferOrderMappingHelper.createBuilderFrom(achTransferOrderEntity).build();
	}

	public static AchTransferOrderEntity toEntity(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {
		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException(
					"PendingFirstSignatureAchTransferOrderMapper.toModel cannot accept null input");
		}

		if (!achTransferOrderAggregateRoot.isPendingFirstSignature()) {
			throw new IllegalStateException(String.format(
					"ACH transfer order aggregate with ID [ %s ] and status [ %s ] cannot mapped to persistence entity as pending first signature",
					achTransferOrderAggregateRoot.getAchTransferOrderId().getId(),
					achTransferOrderAggregateRoot.getStatusString()));
		}

		return AchTransferOrderMappingHelper.createEntityFrom(achTransferOrderAggregateRoot);
	}
}
