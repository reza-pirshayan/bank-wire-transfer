package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;
import java.util.Optional;

import com.pirshayan.domain.model.Validator;

class BankAccountOwner {
	private final String id;
	private final String name;
	private final PersonType personType;
	private final Optional<String> mobileNumber;

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

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public PersonType getPersonType() {
		return personType;
	}

	public Optional<String> getMobileNumber() {
		return mobileNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mobileNumber, name, id, personType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankAccountOwner other = (BankAccountOwner) obj;
		return Objects.equals(mobileNumber, other.mobileNumber) && Objects.equals(name, other.name)
				&& Objects.equals(id, other.id) && personType == other.personType;
	}

}
