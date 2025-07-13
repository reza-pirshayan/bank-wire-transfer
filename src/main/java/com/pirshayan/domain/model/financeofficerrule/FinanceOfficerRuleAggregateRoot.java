package com.pirshayan.domain.model.financeofficerrule;

import com.pirshayan.domain.model.Validator;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToPerformFirstSignException;

public class FinanceOfficerRuleAggregateRoot {
	private final FinanceOfficerRuleId financeOfficerRuleId;
	private final Boolean isAllowedToSignAsFirst;
	private final Boolean isAllowedToSignAsSecond;
	private final Boolean isAllowedToSend;
	private final Long maxFirstSignAmount;
	private final Long maxSecondSignAmount;
	private final Long maxSendAmount;
	private final Position position;

	private FinanceOfficerRuleAggregateRoot(Builder builder) {
		this.financeOfficerRuleId = builder.financeOfficerRuleId;
		this.isAllowedToSignAsFirst = builder.isAllowedToSignAsFirst;
		this.isAllowedToSignAsSecond = builder.isAllowedToSignAsSecond;
		this.isAllowedToSend = builder.isAllowedToSend;
		this.maxFirstSignAmount = Validator.validateTransferAmount(builder.maxFirstSignAmount);
		this.maxSecondSignAmount = Validator.validateTransferAmount(builder.maxSecondSignAmount);
		this.maxSendAmount = Validator.validateTransferAmount(builder.maxSendAmount);
		this.position = builder.position;
	}

	public FinanceOfficerRuleId getFinanceOfficerRuleId() {
		return financeOfficerRuleId;
	}

	public Boolean isAllowedToSignAsFirst() {
		return isAllowedToSignAsFirst;
	}

	public Boolean isAllowedToSignAsSecond() {
		return isAllowedToSignAsSecond;
	}

	public Boolean isAllowedToSend() {
		return isAllowedToSend;
	}

	public Long getMaxFirstSignAmount() {
		return maxFirstSignAmount;
	}

	public Long getMaxSecondSignAmount() {
		return maxSecondSignAmount;
	}

	public Long getMaxSendAmount() {
		return maxSendAmount;
	}

	public Boolean isCeo() {
		return position == Position.CEO;
	}

	public Boolean isCfo() {
		return position == Position.CFO;
	}

	public Boolean isFinanceDirectorGeneral() {
		return position == Position.FINANCE_DIRECTOR_GENERAL;
	}

	public Boolean isDeputyFinanceDirectorGeneral() {
		return position == Position.DEPUTY_FINANCE_DIRECTOR_GENERAL;
	}

	public Boolean isRegionalDirectorGeneral() {
		return position == Position.REGIONAL_DIRECTOR_GENERAL;
	}

	public Boolean isDeputyRegionalDirectorGeneral() {
		return position == Position.DEPUTY_REGIONAL_DIRECTOR_GENERAL;
	}

	public Boolean isBranchManager() {
		return position == Position.BRANCH_MANAGER;
	}

	public Boolean isDeputyBranchFinanceManager() {
		return position == Position.DEPUTY_BRANCH_FINANCE_MANAGER;
	}

	public Boolean isPermanentFinamcePersonnel() {
		return position == Position.PERMANENT_FINANCE_PERSONNEL;
	}

	public Boolean isContranctFinancePersonnel() {
		return position == Position.CONTRACT_FINANCE_PERSONNEL;
	}
	
	public Integer getRank() {
		return position.getRank();
	}
	
	public String getPositionString() {
		return position.name();
	}

	public static class Builder {
		private final FinanceOfficerRuleId financeOfficerRuleId;
		private final Boolean isAllowedToSignAsFirst;
		private final Boolean isAllowedToSignAsSecond;
		private final Boolean isAllowedToSend;
		private final Long maxFirstSignAmount;
		private final Long maxSecondSignAmount;
		private final Long maxSendAmount;
		private Position position;

		public Builder(FinanceOfficerRuleId financeOfficerRuleId, Boolean isAllowedToSignAsFirst,
				Boolean isAllowedToSignAsSecond, Boolean isAllowedToSend, Long maxFirstSignAmount,
				Long maxSecondSignAmount, Long maxSendAmount) {
			this.financeOfficerRuleId = financeOfficerRuleId;
			this.isAllowedToSignAsFirst = isAllowedToSignAsFirst;
			this.isAllowedToSignAsSecond = isAllowedToSignAsSecond;
			this.isAllowedToSend = isAllowedToSend;
			this.maxFirstSignAmount = maxFirstSignAmount;
			this.maxSecondSignAmount = maxSecondSignAmount;
			this.maxSendAmount = maxSendAmount;
			this.position = Position.PERMANENT_FINANCE_PERSONNEL;
		}

		public Builder setCeo() {
			this.position = Position.CEO;
			return this;
		}

		public Builder setCfo() {
			this.position = Position.CFO;
			return this;
		}

		public Builder setFinanceDirectorGeneral() {
			this.position = Position.FINANCE_DIRECTOR_GENERAL;
			return this;
		}

		public Builder setDeputyFinanceDirectorGeneral() {
			this.position = Position.DEPUTY_FINANCE_DIRECTOR_GENERAL;
			return this;
		}

		public Builder setRegionalDirectorGeneral() {
			this.position = Position.REGIONAL_DIRECTOR_GENERAL;
			return this;
		}

		public Builder setDeputyRegionalDirectorGeneral() {
			this.position = Position.DEPUTY_REGIONAL_DIRECTOR_GENERAL;
			return this;
		}

		public Builder setBranchManager() {
			this.position = Position.BRANCH_MANAGER;
			return this;
		}

		public Builder setDeputyBranchFinanceManager() {
			this.position = Position.DEPUTY_BRANCH_FINANCE_MANAGER;
			return this;
		}

		public Builder setPermanentFinamcePersonnel() {
			this.position = Position.PERMANENT_FINANCE_PERSONNEL;
			return this;
		}

		public Builder setContranctFinancePersonnel() {
			this.position = Position.CONTRACT_FINANCE_PERSONNEL;
			return this;
		}

		public FinanceOfficerRuleAggregateRoot build() {
			return new FinanceOfficerRuleAggregateRoot(this);
		}

	}

	public void ensureSufficientPrivilegesForFirstSignature(Long amount) {

		if (!isAllowedToSignAsFirst || maxFirstSignAmount < amount)
			throw new FinanceOfficerNotPrivilegedToPerformFirstSignException(isAllowedToSignAsFirst,
					maxFirstSignAmount);
	}

	public void ensureSufficientPrivilegesForSecondSignature(Long amount) {

		if (!isAllowedToSignAsSecond || maxSecondSignAmount < amount)
			throw new FinanceOfficerNotPrivilegedToPerformFirstSignException(isAllowedToSignAsSecond,
					maxSecondSignAmount);
	}
}
