package com.pirshayan.domain.model.achtransferorder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSignerIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

/**
 * Aggregate Root for managing ACH (Automated Clearing House) transfer orders.
 *
 * This class models all relevant data and behaviors related to a transfer order,
 * including its current status, eligible signers, and signature states.
 *
 * Business rules:
 * - First and second signers must be from predefined candidate lists.
 * - A signer cannot sign both as first and second.
 * - State transitions must happen through controlled methods.
 *
 * This class is immutable. All changes are made by creating a new instance via the Builder.
 */
public class AchTransferOrderAggregateRoot {

	// Unique identifier of this ACH transfer order
	private final AchTransferOrderId achTransferOrderId;

	// Timestamp when the transfer order was received
	private final Long receivedDateTime;

	// Transfer value object containing the details of the actual transaction
	private final Transfer transfer;

	// Current lifecycle status of the ACH order
	private final AchTransferOrderStatus status;

	// Candidates eligible to sign as the first signer
	private final List<FinanceOfficerRuleId> firstSignerCandidateRuleIds;

	// Candidates eligible to sign as the second signer
	private final List<FinanceOfficerRuleId> secondSignerCandidateRuleIds;

	// Optional info of the first signature, if signed
	private final Optional<SignatureInfo> firstSignature;

	// Optional info of the second signature, if signed
	private final Optional<SignatureInfo> secondSignature;

	// Optional cancelation time, if the order was cancelled
	private final Optional<Long> cancelDateTime;

	/**
	 * Private constructor called only from the Builder.
	 */
	private AchTransferOrderAggregateRoot(Builder builder) {
		this.achTransferOrderId = builder.achTransferOrderId;
		this.receivedDateTime = builder.receivedDateTime;
		this.transfer = builder.transfer;
		this.status = builder.status;
		this.firstSignerCandidateRuleIds = builder.firstSignerCandidateRuleIds;
		this.secondSignerCandidateRuleIds = builder.secondSignerCandidateRuleIds;
		this.firstSignature = Optional.ofNullable(builder.firstSignature);
		this.secondSignature = Optional.ofNullable(builder.secondSignature);
		this.cancelDateTime = Optional.ofNullable(builder.cancelDateTime);
	}

	// ------------- Getters (Accessors for domain info) -------------

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

	public LocalDate getTransferDateOfIssue() {
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

	public boolean isTransferDestinationBankAccountOwnerNatural() {
		return transfer.getDestinationBankAccount().getOwner().getPersonType() == PersonType.NATURAL;
	}

	public boolean isTransferDestinationBankAccountOwnerLegal() {
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

	public boolean isCancelled() {
		return status == AchTransferOrderStatus.CANCELLED;
	}

	public boolean isPendingFirstSignature() {
		return status == AchTransferOrderStatus.PENDING_FIRST_SIGNATURE;
	}

	public boolean isPendingSecondSignature() {
		return status == AchTransferOrderStatus.PENDING_SECOND_SIGNATURE;
	}

	public boolean isPendingSend() {
		return status == AchTransferOrderStatus.PENDING_SEND;
	}

	public List<FinanceOfficerRuleId> getFirstSignerCandidateRuleIds() {
		return firstSignerCandidateRuleIds;
	}

	public List<FinanceOfficerRuleId> getSecondSignerCandidateRuleIds() {
		return secondSignerCandidateRuleIds;
	}

	public Optional<Long> getFirstSignatureDateTime() {
		return firstSignature.map(SignatureInfo::getDateTime);
	}

	public Optional<FinanceOfficerRuleId> getFirstSignerRuleId() {
		return firstSignature.map(SignatureInfo::getSignerRuleId);
	}

	public Optional<Long> getSecondSignatureDateTime() {
		return secondSignature.map(SignatureInfo::getDateTime);
	}

	public Optional<FinanceOfficerRuleId> getSecondSignerRuleId() {
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

	/**
	 * Domain behavior: Sign this transfer order as the first signer.
	 */
	public AchTransferOrderAggregateRoot signAsFirst(FinanceOfficerRuleId signerRuleId, Long signDateTime,
			List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds) {

		if (!isPendingFirstSignature()) {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and status [ %s ] cannot be signed as first.",
							getAchTransferOrderId().getId(), status));
		}

		ensureSignerRuleIdIsInCandidateList(signerRuleId, firstSignerCandidateRuleIds);

		return cloneBuilder().setPendingSecondSignature().setFirstSignature(signerRuleId, signDateTime)
				.setFirstSignerCandidateIds(new ArrayList<>())
				.setSecondSignerCandidateIds(refinedSecondSignerCandidateIds).build();
	}

	/**
	 * Domain behavior: Sign this transfer order as the second signer.
	 */
	public AchTransferOrderAggregateRoot signAsSecond(final FinanceOfficerRuleId signerRuleId, Long signDateTime) {
		if (!isPendingSecondSignature()) {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and status [ %s ] cannot be signed as second.",
							getAchTransferOrderId().getId(), status));
		}

		if (getFirstSignatureDateTime().isEmpty() || getFirstSignerRuleId().isEmpty()) {
			throw new IllegalStateException(String.format(
					"ACH transfer order with ID [ %s ] and status [ %s ] must not have null first signature info.",
					getAchTransferOrderId().getId(), getStatusString()));
		}

		if (firstSignature.get().getSignerRuleId().equals(signerRuleId)) {
			throw new AchTransferOrderSigner1AndSigner2CannotBeTheSameException(this);
		}

		ensureSignerRuleIdIsInCandidateList(signerRuleId, secondSignerCandidateRuleIds);

		return cloneBuilder().setPendingSend().setSecondSignature(signerRuleId, signDateTime)
				.setSecondSignerCandidateIds(new ArrayList<>()).build();
	}

	// ----- Internal Helper Methods -----

	/**
	 * Ensures the signer is part of the provided candidate signer list.
	 */
	private void ensureSignerRuleIdIsInCandidateList(FinanceOfficerRuleId signerRuleId,
			List<FinanceOfficerRuleId> candidateSignerList) {
		boolean isCandidate = candidateSignerList.stream().anyMatch(candidateId -> candidateId.equals(signerRuleId));
		if (!isCandidate)
			throw new AchTransferOrderSignerIsNotSignCandidateException(this, signerRuleId);
	}

	/**
	 * Clones the current aggregate state to a builder for further modification.
	 */
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

		if (!firstSignerCandidateRuleIds.isEmpty()) {
			builder.setFirstSignerCandidateIds(firstSignerCandidateRuleIds);
		}

		if (!secondSignerCandidateRuleIds.isEmpty()) {
			builder.setSecondSignerCandidateIds(secondSignerCandidateRuleIds);
		}

		firstSignature.ifPresent(sig -> builder.setFirstSignature(sig.getSignerRuleId(), sig.getDateTime()));
		secondSignature.ifPresent(sig -> builder.setSecondSignature(sig.getSignerRuleId(), sig.getDateTime()));

		transfer.getDestinationBankAccount().getOwner().getMobileNumber()
				.ifPresent(builder::setTransferDestinationBankAccountOwnerMobileNumber);
		cancelDateTime.ifPresent(builder::setCancelDateTime);
		transfer.getDescription().ifPresent(builder::setTransferDescription);
		transfer.getPayId().ifPresent(builder::setTransferPayId);

		return builder;
	}

	// ------------- Builder for Constructing New Aggregate Instances -------------

	/**
	 * Builder class for creating immutable AchTransferOrderAggregateRoot instances.
	 */
	public static class Builder {
		private final AchTransferOrderId achTransferOrderId;
		private final Long receivedDateTime;
		private final String transferId;
		private final Long transferAmount;
		private final LocalDate transferDateOfIssue;
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
		private List<FinanceOfficerRuleId> firstSignerCandidateRuleIds;
		private List<FinanceOfficerRuleId> secondSignerCandidateRuleIds;
		private Long firstSignatureDateTime;
		private FinanceOfficerRuleId firstSignerRuleId;
		private Long secondSignatureDateTime;
		private FinanceOfficerRuleId secondSignerRuleId;
		private Long cancelDateTime;
		private Transfer transfer;
		private SignatureInfo firstSignature;
		private SignatureInfo secondSignature;

		public Builder(AchTransferOrderId achTransferOrderId, Long receivedDateTime, String transferId,
				Long transferAmount, LocalDate transferDateOfIssue, String transferDestinationBankAccountIban,
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
			this.firstSignerCandidateRuleIds = new ArrayList<>();
			this.secondSignerCandidateRuleIds = new ArrayList<>();
		}

		public Builder setDestinationBankAccountOwnerNatural() {
			this.transferDestinationBankAccountOwnerPersonType = PersonType.NATURAL;
			return this;
		}

		public Builder setDestinationBankAccountOwnerLegal() {
			this.transferDestinationBankAccountOwnerPersonType = PersonType.LEGAL;
			return this;
		}

		public Builder setTransferDestinationBankAccountOwnerMobileNumber(String mobileNumber) {
			this.transferDestinationBankAccountOwnerMobileNumber = mobileNumber;
			return this;
		}

		public Builder setTransferDescription(String description) {
			this.transferDescription = description;
			return this;
		}

		public Builder setTransferPayId(String payId) {
			this.transferPayId = payId;
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

		public Builder setFirstSignerCandidateIds(List<FinanceOfficerRuleId> ids) {
			this.firstSignerCandidateRuleIds = ids;
			return this;
		}

		public Builder setSecondSignerCandidateIds(List<FinanceOfficerRuleId> ids) {
			this.secondSignerCandidateRuleIds = ids;
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

		/**
		 * Finalizes and creates the aggregate root instance.
		 */
		public AchTransferOrderAggregateRoot build() {
			BankAccountOwner.Builder ownerBuilder = new BankAccountOwner.Builder(
					transferDestinationBankAccountOwnerId, transferDestinationBankAccountOwnerName,
					transferDestinationBankAccountOwnerPersonType);
			if (transferDestinationBankAccountOwnerMobileNumber != null) {
				ownerBuilder.setMobileNumber(transferDestinationBankAccountOwnerMobileNumber);
			}

			DestinationBankAccount destinationAccount = new DestinationBankAccount(
					transferDestinationBankAccountIban, ownerBuilder.build());

			TransferOwner transferOwner = new TransferOwner(transferOwnerId, transferOwnerName);

			Transfer.Builder transferBuilder = new Transfer.Builder(transferId, transferAmount, transferDateOfIssue,
					destinationAccount, transferOwner, transferChecksum);
			if (transferDescription != null) transferBuilder.setDescription(transferDescription);
			if (transferPayId != null) transferBuilder.setPayId(transferPayId);

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
}
