package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.exception.InvalidDomainObjectException;
import com.pirshayan.domain.model.Validator;

class DestinationBankAccount {
	private final String iban;
	private final BankAccountOwner owner;

	public DestinationBankAccount(String iban, BankAccountOwner owner) throws InvalidDomainObjectException {
		this.iban = Validator.validateIban(iban);
		this.owner = owner;
	}

	public String getIban() {
		return iban;
	}

	public BankAccountOwner getOwner() {
		return owner;
	}

	@Override
	public int hashCode() {
		return Objects.hash(iban, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DestinationBankAccount other = (DestinationBankAccount) obj;
		return Objects.equals(iban, other.iban) && Objects.equals(owner, other.owner);
	}
}
