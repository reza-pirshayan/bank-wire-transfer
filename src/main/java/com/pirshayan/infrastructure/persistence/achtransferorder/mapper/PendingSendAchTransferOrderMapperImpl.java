package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import java.util.Arrays;
import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderAbstract;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SignatureInfo;

import jakarta.inject.Singleton;

@Singleton
public class PendingSendAchTransferOrderMapperImpl implements AchTransferOrderMapper {
	public AchTransferOrderAggregateRoot toModel(AchTransferOrderAbstract entity) {
		if (entity == null) {
			throw new IllegalArgumentException("PendingSendAchTransferOrderMapper.toModel cannot accept null input");
		}

		if (!(entity instanceof SecondSignatureEntity)) {
			throw new IllegalArgumentException(
					String.format("PendingSendSignatureAchTransferOrderMapper.toModel cannot accept input of type %s",
							entity.getClass()));
		}

		SecondSignatureEntity secondSignatureEntity = (SecondSignatureEntity) entity;

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

		FinanceOfficerRuleId firstSignerRuleId = new FinanceOfficerRuleId(
				firstSignatureEntity.getSignatureInfo().getSignerId());

		FinanceOfficerRuleId secondSignerRuleId = new FinanceOfficerRuleId(
				secondSignatureEntity.getSignatureInfo().getSignerId());

		Long firstSignDateTime = firstSignatureEntity.getSignatureInfo().getDateTime();

		AchTransferOrderAggregateRoot pendingFirstSignatureAchTransferOrder = builder
				.setFirstSignerCandidateIds(Arrays.asList(firstSignerRuleId))
				.setSecondSignerCandidateIds(Arrays.asList(secondSignerRuleId)).build();

		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = Arrays.asList(secondSignerRuleId);

		AchTransferOrderAggregateRoot pendingSecondSignatureAchTransferOrder = pendingFirstSignatureAchTransferOrder
				.signAsFirst(firstSignerRuleId, firstSignDateTime, refinedSecondSignerCandidateIds);

		Long secondSignDateTime = firstSignatureEntity.getSignatureInfo().getDateTime();

		return pendingSecondSignatureAchTransferOrder.signAsSecond(secondSignerRuleId, secondSignDateTime);
	}

	public SecondSignatureEntity toEntity(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {

		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException(
					"PendingSendSignatureAchTransferOrderMapper.toEntity cannot accept null input");
		}

		String id = achTransferOrderAggregateRoot.getAchTransferOrderId().getId();
		Long signatureDateTime = achTransferOrderAggregateRoot.getSecondSignatureDateTime().get();
		Long signerId = achTransferOrderAggregateRoot.getSecondSignerRuleId().get().getId();
		SignatureInfo signatureInfo = new SignatureInfo(signatureDateTime, signerId);

		FirstSignatureEntity dummy = new FirstSignatureEntity();
		return new SecondSignatureEntity(id, dummy, signatureInfo);
	}
}
