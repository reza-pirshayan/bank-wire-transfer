package com.pirshayan.domain.model.achtransferorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.FinanceOfficerRuleIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

@AggregateRoot
public class AchTransferOrderAggregateRoot {
	@Identity
	private final AchTransferOrderId achTransferOrderId;
	private final Long receivedDateTime;
	private final Transfer transfer;
	private final AchTransferOrderStatus status;
	private final List<FinanceOfficerRuleId> firstSignerCandidateIds;
	private final List<FinanceOfficerRuleId> secondSignerCandidateIds;
	private final Optional<SignatureInfo> firstSignature;
	private final Optional<SignatureInfo> secondSignature;
	private final Optional<Long> cancelDateTime;

	private AchTransferOrderAggregateRoot(Builder builder) {
		this.achTransferOrderId = builder.achTransferOrderId;
		this.receivedDateTime = builder.receivedDateTime;
		this.transfer = builder.transfer;
		this.status = builder.status;
		this.firstSignerCandidateIds = builder.firstSignerCandidateIds;
		this.secondSignerCandidateIds = builder.secondSignerCandidateIds;
		this.firstSignature = Optional.ofNullable(builder.firstSignature);
		this.secondSignature = Optional.ofNullable(builder.secondSignature);
		this.cancelDateTime = Optional.ofNullable(builder.cancelDateTime);
	}

	public AchTransferOrderId getAchTransferOrderId() {
		return achTransferOrderId;
	}

	public Long getReceivedDateTime() {
		return receivedDateTime;
	}

	public String getTransferId() {
		return transfer.getId();
	}

	public Long getTransferAmount() {
		return transfer.getAmount();
	}

	public Long getTransferDateOfIssue() {
		return transfer.getDateOfIssue();
	}

	public String getTransferDestinationBankAccountIban() {
		return transfer.getDestinationBankAccount().getIban();
	}

	public String getTransferDestinationBankAccountOwnerId() {
		return transfer.getDestinationBankAccount().getOwner().getId();
	}

	public String getTransferDestinationBankAccountOwnerName() {
		return transfer.getDestinationBankAccount().getOwner().getName();
	}

	public Boolean isTransferDestinationBankAccountOwnerNatural() {
		return transfer.getDestinationBankAccount().getOwner().getPersonType() == PersonType.NATURAL;
	}

	public Boolean isTransferDestinationBankAccountOwnerLegal() {
		return transfer.getDestinationBankAccount().getOwner().getPersonType() == PersonType.LEGAL;
	}

	public Optional<String> getTransferDestinationBankAccountOwnerMobileNumber() {
		return transfer.getDestinationBankAccount().getOwner().getMobileNumber();
	}

	public String getTransferOwnerId() {
		return transfer.getDestinationBankAccount().getOwner().getId();
	}

	public String getTransferOwnerName() {
		return transfer.getDestinationBankAccount().getOwner().getName();
	}

	public Integer getTransferChecksum() {
		return transfer.getChecksum();
	}

	public Optional<String> getTransferDescription() {
		return transfer.getDescription();
	}

	public Optional<String> getTransferPayId() {
		return transfer.getPayId();
	}

	public Boolean isCancelled() {
		return status == AchTransferOrderStatus.CANCELLED;
	}

	public Boolean isPendingFirstSignature() {
		return status == AchTransferOrderStatus.PENDING_FIRST_SIGNATURE;
	}

	public Boolean isPendingSecondSignature() {
		return status == AchTransferOrderStatus.PENDING_SECOND_SIGNATURE;
	}

	public Boolean isPendingSend() {
		return status == AchTransferOrderStatus.PENDING_SEND;
	}

	public List<FinanceOfficerRuleId> getFirstSignerCandidateIds() {
		return firstSignerCandidateIds;
	}

	public List<FinanceOfficerRuleId> getSecondSignerCandidateIds() {
		return secondSignerCandidateIds;
	}

	public Optional<Long> getFirstSignatureDateTime() {
		return firstSignature.map(SignatureInfo::getDateTime);
	}

	public Optional<FinanceOfficerRuleId> getFirstSignerId() {
		return firstSignature.map(SignatureInfo::getSignerRuleId);
	}

	public Optional<Long> getSecondSignatureDateTime() {
		return secondSignature.map(SignatureInfo::getDateTime);
	}

	public Optional<FinanceOfficerRuleId> getSecondSignerId() {
		return secondSignature.map(SignatureInfo::getSignerRuleId);
	}

	public Optional<Long> getCancelDateTime() {
		return cancelDateTime;
	}

	public String getStatusString() {
		return status.name();
	}

	public String getTransferDestinationBankAccountOwnerPersonTypeString() {
		return transfer.getDestinationBankAccount().getOwner().getPersonType().name();
	}

	public static class Builder {
		private final AchTransferOrderId achTransferOrderId;
		private final Long receivedDateTime;
		private final String transferId;
		private final Long transferAmount;
		private final Long transferDateOfIssue;
		private final String transferDestinationBankAccountIban;
		private final String transferDestinationBankAccountOwnerId;
		private final String transferDestinationBankAccountOwnerName;
		private final String transferOwnerId;
		private final String transferOwnerName;
		private final Integer transferChecksum;
		private PersonType transferDestinationBankAccountOwnerPersonType;
		private String transferDestinationBankAccountOwnerMobileNumber;
		private String transferDescription;
		private String transferPayId;
		private AchTransferOrderStatus status;
		private List<FinanceOfficerRuleId> firstSignerCandidateIds;
		private List<FinanceOfficerRuleId> secondSignerCandidateIds;
		private Long firstSignatureDateTime;
		private FinanceOfficerRuleId firstSignerRuleId;
		private Long secondSignatureDateTime;
		private FinanceOfficerRuleId secondSignerRuleId;
		private Long cancelDateTime;

		private Transfer transfer;
		private SignatureInfo firstSignature;
		private SignatureInfo secondSignature;

		public Builder(AchTransferOrderId achTransferOrderId, Long receivedDateTime, String transferId,
				Long transferAmount, Long transferDateOfIssue, String transferDestinationBankAccountIban,
				String transferDestinationBankAccountOwnerId, String transferDestinationBankAccountOwnerName,
				String transferOwnerId, String transferOwnerName, Integer transferChecksum) {
			this.achTransferOrderId = achTransferOrderId;
			this.receivedDateTime = receivedDateTime;
			this.transferId = transferId;
			this.transferAmount = transferAmount;
			this.transferDateOfIssue = transferDateOfIssue;
			this.transferDestinationBankAccountIban = transferDestinationBankAccountIban;
			this.transferDestinationBankAccountOwnerId = transferDestinationBankAccountOwnerId;
			this.transferDestinationBankAccountOwnerName = transferDestinationBankAccountOwnerName;
			this.transferOwnerId = transferOwnerId;
			this.transferOwnerName = transferOwnerName;
			this.transferChecksum = transferChecksum;
			this.transferDestinationBankAccountOwnerPersonType = PersonType.NATURAL;
			this.status = AchTransferOrderStatus.PENDING_FIRST_SIGNATURE;
			this.firstSignerCandidateIds = new ArrayList<>();
			this.secondSignerCandidateIds = new ArrayList<>();
		}

		public Builder setDestinationBankAccountOwnerNatural() {
			this.transferDestinationBankAccountOwnerPersonType = PersonType.NATURAL;
			return this;
		}

		public Builder setDestinationBankAccountOwnerLegal() {
			this.transferDestinationBankAccountOwnerPersonType = PersonType.LEGAL;
			return this;
		}

		public Builder setTransferDestinationBankAccountOwnerMobileNumber(
				String transferDestinationBankAccountOwnerMobileNumber) {
			this.transferDestinationBankAccountOwnerMobileNumber = transferDestinationBankAccountOwnerMobileNumber;
			return this;
		}

		public Builder setTransferDescription(String transferDescription) {
			this.transferDescription = transferDescription;
			return this;
		}

		public Builder setTransferPayId(String transferPayId) {
			this.transferPayId = transferPayId;
			return this;
		}

		private Builder setCancelled() {
			this.status = AchTransferOrderStatus.CANCELLED;
			return this;
		}

		private Builder setPendingFirstSignature() {
			this.status = AchTransferOrderStatus.PENDING_FIRST_SIGNATURE;
			return this;
		}

		private Builder setPendingSecondSignature() {
			this.status = AchTransferOrderStatus.PENDING_SECOND_SIGNATURE;
			return this;
		}

		private Builder setPendingSend() {
			this.status = AchTransferOrderStatus.PENDING_SEND;
			return this;
		}

		public Builder setFirstSignerCandidateIds(List<FinanceOfficerRuleId> firstSignerCandidateIds) {
			this.firstSignerCandidateIds = firstSignerCandidateIds;
			return this;
		}

		public Builder setSecondSignerCandidateIds(List<FinanceOfficerRuleId> secondSignerCandidateIds) {
			this.secondSignerCandidateIds = secondSignerCandidateIds;
			return this;
		}

		private Builder setFirstSignature(FinanceOfficerRuleId signerRuleId, Long dateTime) {
			this.firstSignerRuleId = signerRuleId;
			this.firstSignatureDateTime = dateTime;
			return this;
		}

		private Builder setSecondSignature(FinanceOfficerRuleId signerRuleId, Long dateTime) {
			this.secondSignerRuleId = signerRuleId;
			this.secondSignatureDateTime = dateTime;
			return this;
		}

		private Builder setCancelDateTime(Long dateTime) {
			this.cancelDateTime = dateTime;
			return this;
		}

		public AchTransferOrderAggregateRoot build() {
			BankAccountOwner.Builder bankAccountOwnerBuilder = new BankAccountOwner.Builder(
					transferDestinationBankAccountOwnerId, transferDestinationBankAccountOwnerName,
					transferDestinationBankAccountOwnerPersonType);
			if (transferDestinationBankAccountOwnerMobileNumber != null) {
				bankAccountOwnerBuilder.setMobileNumber(transferDestinationBankAccountOwnerMobileNumber);
			}

			DestinationBankAccount destinationBankAccount = new DestinationBankAccount(
					transferDestinationBankAccountIban, bankAccountOwnerBuilder.build());

			TransferOwner transferOwner = new TransferOwner(transferOwnerId, transferOwnerName);

			Transfer.Builder transferBuilder = new Transfer.Builder(transferId, transferAmount, transferDateOfIssue,
					destinationBankAccount, transferOwner, transferChecksum);

			if (transferDescription != null) {
				transferBuilder.setDescription(transferDescription);
			}

			if (transferPayId != null) {
				transferBuilder.setPayId(transferPayId);
			}

			if (firstSignatureDateTime != null && firstSignerRuleId != null) {
				firstSignature = new SignatureInfo(firstSignatureDateTime, firstSignerRuleId);
			}

			if (secondSignatureDateTime != null && secondSignerRuleId != null) {
				secondSignature = new SignatureInfo(secondSignatureDateTime, secondSignerRuleId);
			}

			this.transfer = transferBuilder.build();
			return new AchTransferOrderAggregateRoot(this);
		}
	}

	private void ensureSignerRuleIdIsInCandidateList(FinanceOfficerRuleId signerRuleId,
			List<FinanceOfficerRuleId> candidateSignerList) {
		boolean isCandidate = candidateSignerList.stream().anyMatch(candidateId -> candidateId.equals(signerRuleId));
		if (!isCandidate)
			throw new FinanceOfficerRuleIsNotSignCandidateException(this, signerRuleId);
	}

	public AchTransferOrderAggregateRoot signAsFirst(FinanceOfficerRuleId signerRuleId, Long signDateTime,
			List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds) {

		if (!isPendingFirstSignature()) {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and status [ %s ] cannot be signed as first.",
							getAchTransferOrderId().getId(), status));
		}

		ensureSignerRuleIdIsInCandidateList(signerRuleId, refinedSecondSignerCandidateIds);

		return cloneBuilder().setPendingSecondSignature().setFirstSignature(signerRuleId, signDateTime)
				.setFirstSignerCandidateIds(new ArrayList<>())
				.setSecondSignerCandidateIds(refinedSecondSignerCandidateIds).build();
	}

	public AchTransferOrderAggregateRoot signAsSecond(final FinanceOfficerRuleId signerRuleId, Long signDateTime) {
		if (firstSignature.get().getSignerRuleId().equals(signerRuleId))
			throw new AchTransferOrderSigner1AndSigner2CannotBeTheSameException(this);

		if (!isPendingSecondSignature()) {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and status [ %s ] cannot be signed as second.",
							getAchTransferOrderId().getId(), status));
		}

		ensureSignerRuleIdIsInCandidateList(signerRuleId, secondSignerCandidateIds);

		return cloneBuilder().setPendingSend().setSecondSignature(signerRuleId, signDateTime)
				.setSecondSignerCandidateIds(new ArrayList<>()).build();
	}

	private Builder cloneBuilder() {
		AchTransferOrderAggregateRoot.Builder builder = new AchTransferOrderAggregateRoot.Builder(
				this.achTransferOrderId, this.receivedDateTime, this.transfer.getId(), this.transfer.getAmount(),
				this.transfer.getDateOfIssue(), this.transfer.getDestinationBankAccount().getIban(),
				this.transfer.getDestinationBankAccount().getOwner().getId(),
				this.transfer.getDestinationBankAccount().getOwner().getName(), this.transfer.getOwner().getId(),
				this.transfer.getOwner().getName(), this.transfer.getChecksum());

		switch (status) {
		case CANCELLED -> builder.setCancelled();
		case PENDING_FIRST_SIGNATURE -> builder.setPendingFirstSignature();
		case PENDING_SECOND_SIGNATURE -> builder.setPendingSecondSignature();
		case PENDING_SEND -> builder.setPendingSend();
		}

		if (!firstSignerCandidateIds.isEmpty()) {
			builder.setFirstSignerCandidateIds(firstSignerCandidateIds);
		}

		if (!secondSignerCandidateIds.isEmpty()) {
			builder.setSecondSignerCandidateIds(secondSignerCandidateIds);
		}

		firstSignature.ifPresent(
				signature -> builder.setFirstSignature(signature.getSignerRuleId(), signature.getDateTime()));
		secondSignature.ifPresent(
				signature -> builder.setSecondSignature(signature.getSignerRuleId(), signature.getDateTime()));

		transfer.getDestinationBankAccount().getOwner().getMobileNumber()
				.ifPresent(builder::setTransferDestinationBankAccountOwnerMobileNumber);

		cancelDateTime.ifPresent(builder::setCancelDateTime);
		transfer.getDescription().ifPresent(builder::setTransferDescription);
		transfer.getPayId().ifPresent(builder::setTransferPayId);

		return builder;
	}

}
