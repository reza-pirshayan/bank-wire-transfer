package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.model.Validator;
import com.pirshayan.domain.model.exception.InvalidDomainObjectException;

/**
 * Represents the destination bank account for an ACH (Automated Clearing House)
 * transfer order. This is a value object that includes the IBAN and the account owner's information.
 *
 * It is treated as immutable and compared by its values.
 */
class DestinationBankAccount {

	/**
	 * IBAN (International Bank Account Number) of the recipient bank account.
	 */
	private final String iban;

	/**
	 * Owner of the destination bank account, including identity and person type.
	 */
	private final BankAccountOwner owner;

	/**
	 * Constructs a new DestinationBankAccount value object.
	 *
	 * @param iban  The destination account's IBAN (validated for format and constraints).
	 * @param owner The account owner's information.
	 * @throws InvalidDomainObjectException if the IBAN is invalid.
	 */
	public DestinationBankAccount(String iban, BankAccountOwner owner) throws InvalidDomainObjectException {
		this.iban = Validator.validateIban(iban);
		this.owner = owner;
	}

	/**
	 * Returns the IBAN of the destination account.
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * Returns the owner of the destination account.
	 */
	public BankAccountOwner getOwner() {
		return owner;
	}

	/**
	 * Two DestinationBankAccount objects are equal if both IBAN and owner are equal.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(iban, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		DestinationBankAccount other = (DestinationBankAccount) obj;
		return Objects.equals(iban, other.iban) && Objects.equals(owner, other.owner);
	}
}
