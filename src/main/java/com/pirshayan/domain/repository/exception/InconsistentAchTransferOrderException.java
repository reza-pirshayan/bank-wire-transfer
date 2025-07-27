package com.pirshayan.domain.repository.exception;

import com.pirshayan.domain.model.GeneralException;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

public class InconsistentAchTransferOrderException extends GeneralException {

	private static final long serialVersionUID = 1L;
	private final AchTransferOrderId achTransferOrderId;
	private final Integer status;

	public InconsistentAchTransferOrderException(AchTransferOrderId achTransferOrderId, Integer status) {
		super("Payment-3000", "ACH transfer order is inconsistent");
		this.achTransferOrderId = achTransferOrderId;
		this.status = status;
	}

	public InconsistentAchTransferOrderException(String code, String message, AchTransferOrderId achTransferOrderId,
			Integer status) {
		super(code, message);
		this.achTransferOrderId = achTransferOrderId;
		this.status = status;
	}

	@Override
	public String getErrorDetails() {
		return String.format("ACH transfer order with ID [ %s ] and status [ %d ] is inconsistent in repository",
				achTransferOrderId.getId(), status);
	}

}
