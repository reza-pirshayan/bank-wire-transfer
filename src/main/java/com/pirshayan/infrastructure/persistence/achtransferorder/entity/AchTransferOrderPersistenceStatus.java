package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

public enum AchTransferOrderPersistenceStatus {
	PENDING_FIRST_SIGNATURE(0), PENDING_SECOND_SIGNATURE(1), PENDING_BANK_DISPATCH(2), CANCELLED(-1);

	private final int code;

	AchTransferOrderPersistenceStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
