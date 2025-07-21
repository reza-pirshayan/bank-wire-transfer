package com.pirshayan.domain.model.exception.financeofficer;

import com.pirshayan.domain.model.exception.GeneralException;

public class FinanceOfficerNotPrivilegedToSignAsSecondSignerException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final boolean isAllowedToSignAsSecond;
	private final Long maxSecondSignAmount;

	public FinanceOfficerNotPrivilegedToSignAsSecondSignerException(boolean isAllowedToSignAsSecond,
			Long maxSecondSignAmount) {
		super("Paymnet-103002", "Finance Officer not privileged to sign as the second signer");
		this.isAllowedToSignAsSecond = isAllowedToSignAsSecond;
		this.maxSecondSignAmount = maxSecondSignAmount;
	}

	public boolean isAllowedToSignAsSecond() {
		return isAllowedToSignAsSecond;
	}

	public Long getMaxSecondSignAmount() {
		return maxSecondSignAmount;
	}

	@Override
	public String getErrorDetails() {
		String message = "The finance Officer not privileged to sign as the second signer";
		if (isAllowedToSignAsSecond) {
			message = String.format(
					"The finance officer is authorized to perform second sign for transaction up to [ %s ] Rls.",
					maxSecondSignAmount);
		}
		return message;
	}
}
