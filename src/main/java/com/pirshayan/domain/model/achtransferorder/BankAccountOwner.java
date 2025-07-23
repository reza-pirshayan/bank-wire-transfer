package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;
import java.util.Optional;

import com.pirshayan.domain.model.Validator;

/**
 * Represents the owner of a bank account.
 * This is a value object containing identity information and optional mobile number.
 *
 * Validation logic is applied at construction time to ensure data correctness.
 * Instances are immutable and compared by their content.
 */
class BankAccountOwner {

	/**
	 * National ID of the account owner.
	 * For natural persons: national code.
	 * For legal persons: legal registration code.
	 */
	private final String id;

	/**
	 * Full name of the account owner.
	 */
	private final String name;

	/**
	 * Indicates whether the owner is a natural or legal person.
	 */
	private final PersonType personType;

	/**
	 * Optional mobile number associated with the account owner.
	 */
	private final Optional<String> mobileNumber;

	/**
	 * Constructs an immutable {@code BankAccountOwner} from a builder,
	 * applying validation based on the person type.
	 *
	 * @param builder Builder instance with input fields.
	 */
	private BankAccountOwner(Builder builder) {
		if (builder.personType == PersonType.NATURAL) {
			this.id = Validator.validateNaturalNationalCode(builder.id);
		} else {
			this.id = Validator.validateLegalNationalCode(builder.id);
		}
		this.name = Validator.validateBankAccountOwnerName(builder.name);
		this.personType = builder.personType;
		this.mobileNumber = Optional.ofNullable(builder.mobileNumber)
				.map(Validator::validateBankAccountOwnerMobileNumber);
	}

	/**
	 * Builder class for creating a {@code BankAccountOwner}.
	 * Provides fluent API for optional fields.
	 */
	static class Builder {
		private final String id;
		private final String name;
		private final PersonType personType;
		private String mobileNumber;

		public Builder(String id, String name, PersonType personType) {
			this.id = id;
			this.name = name;
			this.personType = personType;
		}

		public Builder setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
			return this;
		}

		public BankAccountOwner build() {
			return new BankAccountOwner(this);
		}
	}

	/**
	 * Returns the national ID (or legal registration code) of the owner.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the full name of the owner.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of person (natural or legal).
	 */
	public PersonType getPersonType() {
		return personType;
	}

	/**
	 * Returns an optional mobile number for the owner.
	 */
	public Optional<String> getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * Two BankAccountOwner objects are equal if all fields are equal.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(mobileNumber, name, id, personType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		BankAccountOwner other = (BankAccountOwner) obj;
		return Objects.equals(mobileNumber, other.mobileNumber) && Objects.equals(name, other.name)
				&& Objects.equals(id, other.id) && personType == other.personType;
	}
}
