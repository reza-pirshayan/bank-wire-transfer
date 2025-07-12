package com.pirshayan.domain.repository.exception;

import com.pirshayan.domain.exception.GeneralException;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

public class AchTransferOrderNotFoundException extends GeneralException {

	private static final long serialVersionUID = 1L;
	AchTransferOrderId achTransferOrderId;

	public AchTransferOrderNotFoundException(String code, String message, AchTransferOrderId achTransferOrderId) {
		super(code, message);
		this.achTransferOrderId = achTransferOrderId;
	}

	public AchTransferOrderNotFoundException(AchTransferOrderId achTransferOrderId) {
		super("Payment-104003", "ACH transfer order not found");
		this.achTransferOrderId = achTransferOrderId;
	}

	@Override
	public String getErrorDetails() {
		return "ACH Transfer Order with ID [ " + achTransferOrderId.getId() + " ] not found.";
	}
}
