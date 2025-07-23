package com.pirshayan.domain.model.achtransferorder;

import java.time.LocalDate;
import java.util.Optional;

import com.pirshayan.domain.model.Validator;

/**
 * Value Object representing a bank transfer that is part of an ACH Transfer Order.
 *
 * It contains all immutable details of the transfer, including destination account, owner,
 * issue date, amount, checksum, and optional fields like description and payId.
 *
 * This class enforces validation on creation and follows a fluent Builder pattern.
 */
class Transfer {

	// Unique business identifier for this transfer
	private final String id;

	// Monetary amount to be transferred
	private final Long amount;

	// Date when the transfer was issued
	private final LocalDate dateOfIssue;

	// Target bank account that will receive the funds
	private final DestinationBankAccount destinationBankAccount;

	// The owner who initiated the transfer (could be internal or external)
	private final TransferOwner owner;

	// Precomputed checksum value used to verify integrity
	private final Integer checksum;

	// Optional business description of the transfer
	private final Optional<String> description;

	// Optional Pay ID used for integration or reconciliation purposes
	private final Optional<String> payId;

	/**
	 * Private constructor. Use {@link Builder} to create instances.
	 */
	private Transfer(Builder builder) {
		// All validations delegated to Validator utility
		id = Validator.validateTransferId(builder.id);
		amount = Validator.validateTransferAmount(builder.amount);
		dateOfIssue = builder.dateOfIssue;
		destinationBankAccount = builder.destinationBankAccount;
		owner = builder.owner;
		this.checksum = Validator.validateTransferChecksum(builder.checksum);
		this.description = Optional.ofNullable(builder.description)
				.map(Validator::validateDescription);
		this.payId = Optional.ofNullable(builder.payId)
				.map(Validator::validatePayId);
	}

	// ----------------- Getters (value object is immutable) -----------------

	public String getId() {
		return id;
	}

	public Long getAmount() {
		return amount;
	}

	public LocalDate getDateOfIssue() {
		return dateOfIssue;
	}

	public DestinationBankAccount getDestinationBankAccount() {
		return destinationBankAccount;
	}

	public TransferOwner getOwner() {
		return owner;
	}

	public Integer getChecksum() {
		return checksum;
	}

	public Optional<String> getDescription() {
		return description;
	}

	public Optional<String> getPayId() {
		return payId;
	}

	/**
	 * Builder for safely creating {@link Transfer} instances with validation.
	 *
	 * Mandatory fields are set via constructor; optional ones via fluent methods.
	 */
	static class Builder {
		private final String id;
		private final Long amount;
		private final LocalDate dateOfIssue;
		private final DestinationBankAccount destinationBankAccount;
		private final TransferOwner owner;
		private final Integer checksum;

		private String description;
		private String payId;

		/**
		 * Constructor with all required fields.
		 *
		 * @param id Transfer ID
		 * @param amount Transfer amount
		 * @param dateOfIssue Date the transfer is issued
		 * @param destinationBankAccount Target account to receive funds
		 * @param owner Initiator of the transfer
		 * @param checksum Precomputed checksum
		 */
		public Builder(String id, Long amount, LocalDate dateOfIssue,
				DestinationBankAccount destinationBankAccount, TransferOwner owner, Integer checksum) {
			this.id = id;
			this.amount = amount;
			this.dateOfIssue = dateOfIssue;
			this.destinationBankAccount = destinationBankAccount;
			this.owner = owner;
			this.checksum = checksum;
		}

		/**
		 * Sets an optional description for the transfer.
		 */
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Sets an optional pay ID for integration or reconciliation.
		 */
		public Builder setPayId(String payId) {
			this.payId = payId;
			return this;
		}

		/**
		 * Builds a validated Transfer object.
		 */
		public Transfer build() {
			return new Transfer(this);
		}
	}
}
