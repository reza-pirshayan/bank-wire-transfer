package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.model.Validator;


public class AchTransferOrderId {
	private final String id;

	public AchTransferOrderId(String id) {
		this.id = Validator.validateAchTransferOrderId(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AchTransferOrderId other = (AchTransferOrderId) obj;
		return Objects.equals(id, other.id);
	}

}
