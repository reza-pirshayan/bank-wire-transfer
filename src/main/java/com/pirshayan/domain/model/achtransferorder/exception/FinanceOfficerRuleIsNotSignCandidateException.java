package com.pirshayan.domain.model.achtransferorder.exception;

import com.pirshayan.domain.exception.GeneralException;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

public class FinanceOfficerRuleIsNotSignCandidateException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final AchTransferOrderAggregateRoot achTransferOrder;
	private final FinanceOfficerRuleId signerRuleId;

	public FinanceOfficerRuleIsNotSignCandidateException(AchTransferOrderAggregateRoot achTransferOrder,
			FinanceOfficerRuleId signerRuleId) {
		super("Payment-103002", "Finance officer is not a sign candidate");
		this.achTransferOrder = achTransferOrder;
		this.signerRuleId = signerRuleId;
	}

	public AchTransferOrderAggregateRoot getAchTransferOrder() {
		return achTransferOrder;
	}

	public FinanceOfficerRuleId getSignerRuleId() {
		return signerRuleId;
	}

	@Override
	public String getErrorDetails() {
		return String.format(
				"Finance officer rule with ID [ %s ] is not a sign candidate for ACH transfer order with ID  [ %s ]",
				signerRuleId, achTransferOrder.getAchTransferOrderId().getId());
	}

}
