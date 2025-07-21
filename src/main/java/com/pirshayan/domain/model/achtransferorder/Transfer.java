package com.pirshayan.domain.model.achtransferorder;

import java.time.LocalDate;
import java.util.Optional;

import com.pirshayan.domain.model.Validator;

class Transfer {
	private final String id;
	private final Long amount;
	private final LocalDate dateOfIssue;
	private final DestinationBankAccount destinationBankAccount;
	private final TransferOwner owner;
	private final Integer checksum;
	private final Optional<String> description;
	private final Optional<String> payId;

	private Transfer(Builder builder) {
		id = Validator.validateTransferId(builder.id);
		amount = Validator.validateTransferAmount(builder.amount);
		dateOfIssue = builder.dateOfIssue;
		destinationBankAccount = builder.destinationBankAccount;
		owner = builder.owner;
		this.checksum = Validator.validateTransferChecksum(builder.checksum);
		this.description = Optional.ofNullable(builder.description)
				.map(Validator::validateDescription);
		this.payId = Optional.ofNullable(builder.payId).map(Validator::validatePayId);
	}

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

	static class Builder {
		private final String id;
		private final Long amount;
		private final LocalDate dateOfIssue;
		private final DestinationBankAccount destinationBankAccount;
		private final TransferOwner owner;
		private final Integer checksum;
		private String description;
		private String payId;

		public Builder(String id, Long amount, LocalDate dateOfIssue, DestinationBankAccount destinationBankAccount,
				TransferOwner owner, Integer checksum) {
			this.id = id;
			this.amount = amount;
			this.dateOfIssue = dateOfIssue;
			this.destinationBankAccount = destinationBankAccount;
			this.owner = owner;
			this.checksum = checksum;
		}

		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setPayId(String payId) {
			this.payId = payId;
			return this;
		}

		public Transfer build() {
			return new Transfer(this);
		}
	}

}
