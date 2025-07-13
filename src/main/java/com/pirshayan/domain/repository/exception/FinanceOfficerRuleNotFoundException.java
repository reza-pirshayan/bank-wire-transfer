package com.pirshayan.domain.repository.exception;

import com.pirshayan.domain.exception.GeneralException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

public class FinanceOfficerRuleNotFoundException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final FinanceOfficerRuleId financeOfficerRuleId;

	public FinanceOfficerRuleNotFoundException(String code, String message, FinanceOfficerRuleId financeOfficerRuleId) {
		super(code, message);
		this.financeOfficerRuleId = financeOfficerRuleId;
	}

	public FinanceOfficerRuleNotFoundException(FinanceOfficerRuleId financeOfficerRuleId) {
		super("Payment-103006", "Finance officer (rule) not found");
		this.financeOfficerRuleId = financeOfficerRuleId;
	}

	public FinanceOfficerRuleId getFinanceOfficerRuleId() {
		return financeOfficerRuleId;
	}

	@Override
	public String getErrorDetails() {
		return String.format("Finance officer id [ %s ] is not found", financeOfficerRuleId.getId());
	}
}
