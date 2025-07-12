package com.pirshayan.domain.model.financeofficerrule.exception;

import com.pirshayan.domain.exception.GeneralException;

public class FinanceOfficerNotPrivilegedToPerformFirstSignException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final Boolean hasFirstSignaturePermission;
	private final Long maxAmountToSign;

	public FinanceOfficerNotPrivilegedToPerformFirstSignException(Boolean hasFirstSignaturePermission,
			Long maxAmountToSign) {
		super("Paymnet-103002", "finance officer has not priviledges to perform the first sign");
		this.hasFirstSignaturePermission = hasFirstSignaturePermission;
		this.maxAmountToSign = maxAmountToSign;
	}

	public Boolean hasFirstSignaturePermission() {
		return hasFirstSignaturePermission;
	}

	public Long getMaxAmountToSign() {
		return maxAmountToSign;
	}

	@Override
	public String getErrorDetails() {
		String message = "The signer does not have permission to perform the first signature";
		if (hasFirstSignaturePermission) {
			message = String.format(
					"The finance officer is authorized to perform first sign for transaction up to [ %s ] Rls",
					maxAmountToSign);
		}
		return message;
	}
}
