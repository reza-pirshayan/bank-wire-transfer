package com.pirshayan.domain.model.achtransferorder.exception;

import com.pirshayan.domain.exception.GeneralException;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;

public class AchTransferOrderSigner1AndSigner2CannotBeTheSameException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final AchTransferOrderAggregateRoot achTransferOrder;

	public AchTransferOrderSigner1AndSigner2CannotBeTheSameException(AchTransferOrderAggregateRoot achTransferOrder) {
		super("Payment-104001", "Signer1 and Signer2 cannot be the same");
		this.achTransferOrder = achTransferOrder;
	}

	public AchTransferOrderAggregateRoot getAchTransferOrder() {
		return achTransferOrder;
	}

	@Override
	public String getErrorDetails() {
		return String.format("signer1 and signer2 cannot be the same for ACH transfer order [ %s ] signer id as [ %s ]",
				achTransferOrder.getAchTransferOrderId().getId(), achTransferOrder.getFirstSignerRuleId().get().getId());
	}
}
