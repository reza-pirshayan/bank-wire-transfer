package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SignatureInfo;

public class PendingSendAchTransferOrderMapper {
	public static AchTransferOrderAggregateRoot toModel(SecondSignatureEntity secondSignatureEntity) {
		if (secondSignatureEntity == null) {
			throw new IllegalArgumentException("PendingSendAchTransferOrderMapper.toModel cannot accept null input");
		}

		FirstSignatureEntity firstSignatureEntity = secondSignatureEntity.getFirstSignatureEntity();
		if (firstSignatureEntity == null) {
			throw new IllegalStateException(String.format(
					"SecondSignatureEntity instance with ID [ %s ] failed to map to AggregateRoot because secondSignatureEntity.getFirstSignatureEntity() returns null",
					secondSignatureEntity.getOrderId()));
		}

		AchTransferOrderEntity achTransferOrderEntity = firstSignatureEntity.getAchTransferOrderEntity();
		if (achTransferOrderEntity == null) {
			throw new IllegalStateException(String.format(
					"SecondSignatureEntity instance with ID [ %s ] failed to map to AggregateRoot because secondSignatureEntity.getFirstSignatureEntity.getAchTransfeEntity() returns null",
					firstSignatureEntity.getOrderId()));
		}

		AchTransferOrderAggregateRoot.Builder builder = AchTransferOrderMappingHelper
				.createBuilderFrom(achTransferOrderEntity);

		AchTransferOrderAggregateRoot pendingFirstSignatureAchTransferOrder = builder.build();

		FinanceOfficerRuleId firstSignerRuleId = new FinanceOfficerRuleId(
				firstSignatureEntity.getSignatureInfo().getSignerId());

		Long firstSignDateTime = firstSignatureEntity.getSignatureInfo().getDateTime();

		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = achTransferOrderEntity
				.getFirstSignerCandidateEntities().stream().map(f -> new FinanceOfficerRuleId(f.getId())).toList();

		AchTransferOrderAggregateRoot pendingSecondSignatureAchTransferOrder = pendingFirstSignatureAchTransferOrder
				.signAsFirst(firstSignerRuleId, firstSignDateTime, refinedSecondSignerCandidateIds);

		FinanceOfficerRuleId secondSignerRuleId = new FinanceOfficerRuleId(
				secondSignatureEntity.getSignatureInfo().getSignerId());

		Long secondSignDateTime = firstSignatureEntity.getSignatureInfo().getDateTime();

		return pendingSecondSignatureAchTransferOrder.signAsSecond(secondSignerRuleId, secondSignDateTime);
	}

	public static SecondSignatureEntity toEntity(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot,
			FirstSignatureEntity firstSignatureEntity) {

		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException(
					"PendingSendSignatureAchTransferOrderMapper.toEntity cannot accept null input");
		}

		if (firstSignatureEntity == null) {
			throw new IllegalArgumentException(
					"PendingSendSignatureAchTransferOrderMapper.toEntity cannot accept null input");
		}

		String id = firstSignatureEntity.getOrderId();
		Long signatureDateTime = achTransferOrderAggregateRoot.getSecondSignatureDateTime().get();
		Long signerId = achTransferOrderAggregateRoot.getSecondSignerRuleId().get().getId();
		SignatureInfo signatureInfo = new SignatureInfo(signatureDateTime, signerId);
		return new SecondSignatureEntity(id, firstSignatureEntity, signatureInfo);
	}
}
