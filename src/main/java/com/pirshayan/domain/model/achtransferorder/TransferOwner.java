package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.model.Validator;
import com.pirshayan.domain.model.exception.InvalidDomainObjectException;

class TransferOwner {
	private final String id;
	private final String name;

	TransferOwner(String id, String name) throws InvalidDomainObjectException {
		/*
		 * nationalCode cannot be validated here due it's dependency to the person type,
		 * therefore validation check will occur where both the national code and the
		 * person type are simultaneously available
		 */
		this.id = id;
		this.name = Validator.validateTransferOwnerName(name);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransferOwner other = (TransferOwner) obj;
		return Objects.equals(name, other.name) && Objects.equals(id, other.id);
	}
}
