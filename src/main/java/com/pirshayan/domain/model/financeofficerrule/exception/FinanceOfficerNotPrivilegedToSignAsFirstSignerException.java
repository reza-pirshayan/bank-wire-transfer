package com.pirshayan.domain.model.financeofficerrule.exception;

import com.pirshayan.domain.model.GeneralException;

public class FinanceOfficerNotPrivilegedToSignAsFirstSignerException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final boolean isAllowedToSignAsFirst;
	private final Long maxFirstSignAmount;

	public FinanceOfficerNotPrivilegedToSignAsFirstSignerException(boolean isAllowedToSignAsFirst,
			Long maxFirstSignAmount) {
		super("Paymnet-101001", "Finance Officer not privileged to sign as the first signer");
		this.isAllowedToSignAsFirst = isAllowedToSignAsFirst;
		this.maxFirstSignAmount = maxFirstSignAmount;
	}

	public boolean isAllowedToSignAsFirst() {
		return isAllowedToSignAsFirst;
	}

	public Long getMaxFirstSignAmount() {
		return maxFirstSignAmount;
	}

	@Override
	public String getErrorDetails() {
		String message = "The finance Officer not privileged to sign as the first signer";
		if (isAllowedToSignAsFirst) {
			message = String.format(
					"The finance officer is authorized to perform first sign for transaction up to [ %s ] Rls.",
					maxFirstSignAmount);
		}
		return message;
	}
}
