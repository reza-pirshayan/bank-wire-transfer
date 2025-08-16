package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import java.util.Arrays;
import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderAbstract;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignatureEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SignatureInfo;

import jakarta.inject.Singleton;

@Singleton
public class PendingSecondSignatureAchTransferOrderMapperImpl implements AchTransferOrderMapper {

	public AchTransferOrderAggregateRoot toModel(AchTransferOrderAbstract entity) {
		if (entity == null) {
			throw new IllegalArgumentException(
					"PendingSecondSignatureAchTransferOrderMapper.toModel cannot accept null input");
		}

		if (!(entity instanceof FirstSignatureEntity)) {
			throw new IllegalArgumentException(
					String.format("PendingSecondSignatureAchTransferOrderMapper.toModel cannot accept input of type %s",
							entity.getClass()));
		}

		FirstSignatureEntity firstSignatureEntity = (FirstSignatureEntity) entity;
		AchTransferOrderEntity achTransferOrderEntity = firstSignatureEntity.getAchTransferOrderEntity();
		if (achTransferOrderEntity == null) {
			throw new IllegalStateException(String.format(
					"FirstSignatureEntity instance with ID [ %s ] failed to map to AggregateRoot because firstSignatureEntity.getAchTransferEntity() returns null",
					firstSignatureEntity.getOrderId()));
		}

		FinanceOfficerRuleId firstSignerRuleId = new FinanceOfficerRuleId(
				firstSignatureEntity.getSignatureInfo().getSignerId());

		AchTransferOrderAggregateRoot.Builder builder = AchTransferOrderMappingHelper
				.createBuilderFrom(achTransferOrderEntity);

		AchTransferOrderAggregateRoot pendingFirstSignatureAchTransferOrder = builder
				.setFirstSignerCandidateIds(Arrays.asList(firstSignerRuleId)).build();

		Long signDateTime = firstSignatureEntity.getSignatureInfo().getDateTime();

		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = achTransferOrderEntity
				.getSecondSignerCandidateEntities().stream().map(f -> new FinanceOfficerRuleId(f.getSignerId()))
				.toList();

		return pendingFirstSignatureAchTransferOrder.signAsFirst(firstSignerRuleId, signDateTime,
				refinedSecondSignerCandidateIds);
	}

	public FirstSignatureEntity toEntity(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {

		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException(
					"PendingSecondSignatureAchTransferOrderMapper.toEntity cannot accept null input");
		}

		if (!achTransferOrderAggregateRoot.isPendingSecondSignature()) {
			throw new IllegalStateException(String.format(
					"ACH transfer order aggregate with ID [ %s ] and status [ %s ] cannot mapped to persistence entity as pending second signature",
					achTransferOrderAggregateRoot.getAchTransferOrderId().getId(),
					achTransferOrderAggregateRoot.getStatusString()));
		}

		String id = achTransferOrderAggregateRoot.getAchTransferOrderId().getId();
		Long signatureDateTime = achTransferOrderAggregateRoot.getFirstSignatureDateTime().get();
		Long signerId = achTransferOrderAggregateRoot.getFirstSignerRuleId().get().getId();
		SignatureInfo signatureInfo = new SignatureInfo(signatureDateTime, signerId);
		AchTransferOrderEntity dummy = new AchTransferOrderEntity();
		return new FirstSignatureEntity(id, dummy, signatureInfo);
	}

}
