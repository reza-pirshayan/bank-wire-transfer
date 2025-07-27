package com.pirshayan.domain.model.financeofficerrule;

import java.util.Objects;

import com.pirshayan.domain.model.Validator;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;

/**
 * Aggregate Root representing the signing and sending privileges of a finance officer.
 *
 * This class models a rule associated with a specific finance officer role, determining
 * whether the officer can sign as first or second signer, send transfers, and their permitted thresholds.
 */
public class FinanceOfficerRuleAggregateRoot {

    private final FinanceOfficerRuleId financeOfficerRuleId;
    private final boolean isAllowedToSignAsFirst;
    private final boolean isAllowedToSignAsSecond;
    private final boolean isAllowedToSend;
    private final Long maxFirstSignAmount;
    private final Long maxSecondSignAmount;
    private final Long maxSendAmount;
    private final Position position;

    /**
     * Private constructor to enforce use of the Builder.
     */
    private FinanceOfficerRuleAggregateRoot(Builder builder) {
        this.financeOfficerRuleId = builder.financeOfficerRuleId;
        this.isAllowedToSignAsFirst = builder.isAllowedToSignAsFirst;
        this.isAllowedToSignAsSecond = builder.isAllowedToSignAsSecond;
        this.isAllowedToSend = builder.isAllowedToSend;
        this.maxFirstSignAmount = Validator.validateMaxFirstSignAmount(builder.maxFirstSignAmount);
        this.maxSecondSignAmount = Validator.validateMaxSecondSignAmount(builder.maxSecondSignAmount);
        this.maxSendAmount = Validator.validateMaxSendAmount(builder.maxSendAmount);
        this.position = builder.position;
    }

    // === Getters ===

    public FinanceOfficerRuleId getFinanceOfficerRuleId() {
        return financeOfficerRuleId;
    }

    public boolean isAllowedToSignAsFirst() {
        return isAllowedToSignAsFirst;
    }

    public boolean isAllowedToSignAsSecond() {
        return isAllowedToSignAsSecond;
    }

    public boolean isAllowedToSend() {
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

    public String getPositionString() {
        return position.name();
    }

    public Integer getRank() {
        return position.getRank();
    }

    // === Position check helpers ===

    public boolean isCeo() { return position == Position.CEO; }
    public boolean isCfo() { return position == Position.CFO; }
    public boolean isFinanceDirectorGeneral() { return position == Position.FINANCE_DIRECTOR_GENERAL; }
    public boolean isDeputyFinanceDirectorGeneral() { return position == Position.DEPUTY_FINANCE_DIRECTOR_GENERAL; }
    public boolean isRegionalDirectorGeneral() { return position == Position.REGIONAL_DIRECTOR_GENERAL; }
    public boolean isDeputyRegionalDirectorGeneral() { return position == Position.DEPUTY_REGIONAL_DIRECTOR_GENERAL; }
    public boolean isBranchManager() { return position == Position.BRANCH_MANAGER; }
    public boolean isDeputyBranchFinanceManager() { return position == Position.DEPUTY_BRANCH_FINANCE_MANAGER; }
    public boolean isPermanentFinamcePersonnel() { return position == Position.PERMANENT_FINANCE_PERSONNEL; }
    public boolean isContranctFinancePersonnel() { return position == Position.CONTRACT_FINANCE_PERSONNEL; }

    /**
     * Ensures the officer has the required privileges to sign as the first signer for the given amount.
     *
     * @throws FinanceOfficerNotPrivilegedToSignAsFirstSignerException if not permitted
     */
    public void ensureSufficientPrivilegesForFirstSignature(Long amount) {
        if (!isAllowedToSignAsFirst || maxFirstSignAmount < amount) {
            throw new FinanceOfficerNotPrivilegedToSignAsFirstSignerException(isAllowedToSignAsFirst, maxFirstSignAmount);
        }
    }

    /**
     * Ensures the officer has the required privileges to sign as the second signer for the given amount.
     *
     * @throws FinanceOfficerNotPrivilegedToSignAsSecondSignerException if not permitted
     */
    public void ensureSufficientPrivilegesForSecondSignature(Long amount) {
        if (!isAllowedToSignAsSecond || maxSecondSignAmount < amount) {
            throw new FinanceOfficerNotPrivilegedToSignAsSecondSignerException(isAllowedToSignAsSecond, maxSecondSignAmount);
        }
    }

    // === Builder pattern ===

    /**
     * Builder for {@link FinanceOfficerRuleAggregateRoot}
     */
    public static class Builder {
        private final FinanceOfficerRuleId financeOfficerRuleId;
        private final Boolean isAllowedToSignAsFirst;
        private final Boolean isAllowedToSignAsSecond;
        private final Boolean isAllowedToSend;
        private final Long maxFirstSignAmount;
        private final Long maxSecondSignAmount;
        private final Long maxSendAmount;
        private Position position;

        /**
         * Required fields for a Finance Officer Rule.
         */
        public Builder(FinanceOfficerRuleId financeOfficerRuleId,
                       Boolean isAllowedToSignAsFirst,
                       Boolean isAllowedToSignAsSecond,
                       Boolean isAllowedToSend,
                       Long maxFirstSignAmount,
                       Long maxSecondSignAmount,
                       Long maxSendAmount) {
            this.financeOfficerRuleId = financeOfficerRuleId;
            this.isAllowedToSignAsFirst = isAllowedToSignAsFirst;
            this.isAllowedToSignAsSecond = isAllowedToSignAsSecond;
            this.isAllowedToSend = isAllowedToSend;
            this.maxFirstSignAmount = maxFirstSignAmount;
            this.maxSecondSignAmount = maxSecondSignAmount;
            this.maxSendAmount = maxSendAmount;
            this.position = Position.PERMANENT_FINANCE_PERSONNEL;
        }

        // === Fluent setters for officer roles ===

        public Builder setCeo() { this.position = Position.CEO; return this; }
        public Builder setCfo() { this.position = Position.CFO; return this; }
        public Builder setFinanceDirectorGeneral() { this.position = Position.FINANCE_DIRECTOR_GENERAL; return this; }
        public Builder setDeputyFinanceDirectorGeneral() { this.position = Position.DEPUTY_FINANCE_DIRECTOR_GENERAL; return this; }
        public Builder setRegionalDirectorGeneral() { this.position = Position.REGIONAL_DIRECTOR_GENERAL; return this; }
        public Builder setDeputyRegionalDirectorGeneral() { this.position = Position.DEPUTY_REGIONAL_DIRECTOR_GENERAL; return this; }
        public Builder setBranchManager() { this.position = Position.BRANCH_MANAGER; return this; }
        public Builder setDeputyBranchFinanceManager() { this.position = Position.DEPUTY_BRANCH_FINANCE_MANAGER; return this; }
        public Builder setPermanentFinancePersonnel() { this.position = Position.PERMANENT_FINANCE_PERSONNEL; return this; }
        public Builder setContranctFinancePersonnel() { this.position = Position.CONTRACT_FINANCE_PERSONNEL; return this; }

        public FinanceOfficerRuleAggregateRoot build() {
            return new FinanceOfficerRuleAggregateRoot(this);
        }
    }

    // === Equality based on aggregate identity ===

    @Override
    public int hashCode() {
        return Objects.hash(financeOfficerRuleId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FinanceOfficerRuleAggregateRoot other = (FinanceOfficerRuleAggregateRoot) obj;
        return Objects.equals(financeOfficerRuleId, other.financeOfficerRuleId);
    }
}
