package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.FirstSignerCandidateEntity;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.SecondSignerCandidateEntity;

public class AchTransferOrderMappingHelper {

	public static AchTransferOrderAggregateRoot.Builder createBuilderFrom(AchTransferOrderEntity entity) {
		var builder = new AchTransferOrderAggregateRoot.Builder(new AchTransferOrderId(entity.getOrderId()),
				entity.getReceivedDateTime(), entity.getTransferNo(), entity.getAmount(), entity.getDateOfIssue(),
				entity.getDestinationIban(), entity.getAccountOwnerNationalCode(), entity.getAccountOwnerName(),
				entity.getTransferOwnerNationalCode(), entity.getTransferOwnerName(), entity.getChecksum());

		setAccountOwnerPersonType(builder, entity);
		setOptionalFields(builder, entity);
		setSignerCandidates(builder, entity);

		return builder;
	}

	public static AchTransferOrderEntity createEntityFrom(AchTransferOrderAggregateRoot root) {
		String status = mapStatus(root);

		return new AchTransferOrderEntity(root.getAchTransferOrderId().getId(), root.getReceivedDateTime(),
				root.getTransferId(), root.getTransferAmount(), root.getTransferDateOfIssue(),
				root.getTransferDestinationBankAccountIban(), root.getTransferDestinationBankAccountOwnerId(),
				root.getTransferDestinationBankAccountOwnerName(),
				root.getTransferDestinationBankAccountOwnerPersonTypeString(),
				root.getTransferDestinationBankAccountOwnerMobileNumber().orElse(null), root.getTransferOwnerId(),
				root.getTransferOwnerName(), root.getTransferChecksum(), root.getTransferDescription().orElse(null),
				root.getTransferPayId().orElse(null), Integer.parseInt(status));

	}

	private static void setSignerCandidates(AchTransferOrderAggregateRoot.Builder builder,
			AchTransferOrderEntity entity) {
		List<FirstSignerCandidateEntity> firstSignerCandidates = entity.getFirstSignerCandidateEntities();

		if (!firstSignerCandidates.isEmpty()) {
			builder.setFirstSignerCandidateIds(
					firstSignerCandidates.stream().map(f -> new FinanceOfficerRuleId(f.getSignerId())).toList());
		}

		List<SecondSignerCandidateEntity> secondSigerCandidates = entity.getSecondSignerCandidateEntities();

		if (!secondSigerCandidates.isEmpty()) {
			builder.setSecondSignerCandidateIds(
					secondSigerCandidates.stream().map(f -> new FinanceOfficerRuleId(f.getSignerId())).toList());
		}
	}

	private static void setAccountOwnerPersonType(AchTransferOrderAggregateRoot.Builder builder,
			AchTransferOrderEntity entity) {
		String personType = entity.getAccountOwnerPersonType();
		if (personType == null) {
			throw new IllegalStateException(String.format(
					"ACH transfer order entity with ID [%s] and status [%s] cannot have empty account owner's person type",
					entity.getOrderId(), entity.getStatus()));
		}

		switch (personType) {
		case "NATURAL" -> builder.setDestinationBankAccountOwnerNatural();
		case "LEGAL" -> builder.setDestinationBankAccountOwnerLegal();
		default -> throw new IllegalStateException(
				String.format("ACH transfer order entity with ID [%s] and status [%s] has unknown person type [%s]",
						entity.getOrderId(), entity.getStatus(), personType));
		}
	}

	private static void setOptionalFields(AchTransferOrderAggregateRoot.Builder builder,
			AchTransferOrderEntity entity) {
		if (entity.getAccountOwnerMobileNumber() != null) {
			builder.setTransferDestinationBankAccountOwnerMobileNumber(entity.getAccountOwnerMobileNumber());
		}

		if (entity.getTranserDescriprion() != null) {
			builder.setTransferDescription(entity.getTranserDescriprion());
		}

		if (entity.getTransferPayId() != null) {
			builder.setTransferPayId(entity.getTransferPayId());
		}
	}

	private static String mapStatus(AchTransferOrderAggregateRoot root) {
		if (root.isPendingFirstSignature())
			return "0";
		if (root.isPendingSecondSignature())
			return "1";
		if (root.isPendingSend())
			return "2";
		if (root.isCancelled())
			return "-1";

		throw new IllegalStateException(String.format("ACH transfer order with ID [%s] has unknown status: %s",
				root.getAchTransferOrderId().getId(), root.getStatusString()));
	}

}
