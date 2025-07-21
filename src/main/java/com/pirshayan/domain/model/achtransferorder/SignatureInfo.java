package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

class SignatureInfo {
	private final Long dateTime;
	private final FinanceOfficerRuleId signerRuleId;

	public SignatureInfo(Long dateTime, FinanceOfficerRuleId signerRuleId) {
		this.dateTime = dateTime;
		this.signerRuleId = signerRuleId;
	}

	public Long getDateTime() {
		return dateTime;
	}

	public FinanceOfficerRuleId getSignerRuleId() {
		return signerRuleId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateTime, signerRuleId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignatureInfo other = (SignatureInfo) obj;
		return Objects.equals(dateTime, other.dateTime) && Objects.equals(signerRuleId, other.signerRuleId);
	}
}
