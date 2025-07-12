package com.pirshayan.domain.service.exception;

import com.pirshayan.domain.exception.GeneralException;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;

public class SecondSignersRankLowerThanFirstSignersRankException extends GeneralException {
	private static final long serialVersionUID = 1L;
	private final AchTransferOrderAggregateRoot achTransferOrder;
	private final FinanceOfficerRuleAggregateRoot firstSignerRule;
	private final FinanceOfficerRuleAggregateRoot secondSignerRule;

	public SecondSignersRankLowerThanFirstSignersRankException(AchTransferOrderAggregateRoot achTransferOrder,
			FinanceOfficerRuleAggregateRoot firstSignerRule, FinanceOfficerRuleAggregateRoot secondSignerRule) {
		super("Payment-200001", "ACH transfer order's second signer's rank is less than the one of the first signer");
		this.achTransferOrder = achTransferOrder;
		this.firstSignerRule = firstSignerRule;
		this.secondSignerRule = secondSignerRule;
	}

	public AchTransferOrderAggregateRoot getAchTransferOrder() {
		return achTransferOrder;
	}

	public FinanceOfficerRuleAggregateRoot getFirstSignerRule() {
		return firstSignerRule;
	}

	public FinanceOfficerRuleAggregateRoot getSecondSignerRule() {
		return secondSignerRule;
	}

	@Override
	public String getErrorDetails() {
		return String.format(
				"ACH transfer order with ID [ %s ] and first signer's POSITION [ %s ] cannot be signed by a second signer with POSITION [ %s ]",
				getAchTransferOrder().getAchTransferOrderId().getId(), getFirstSignerRule().getPositionString(),
				getSecondSignerRule().getPositionString());
	}
}
