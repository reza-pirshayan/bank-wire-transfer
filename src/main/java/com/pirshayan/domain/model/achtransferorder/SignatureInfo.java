package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

/**
 * Represents metadata about a signature on an ACH transfer order.
 *
 * This value object captures who signed and when.
 * It is immutable and equality is based on both dateTime and signerRuleId.
 */
class SignatureInfo {

    /**
     * The timestamp of when the signature was made (epoch milliseconds).
     */
    private final Long dateTime;

    /**
     * The identifier of the finance officer rule that performed the signature.
     */
    private final FinanceOfficerRuleId signerRuleId;

    /**
     * Constructs an immutable SignatureInfo object.
     *
     * @param dateTime      The timestamp of the signature.
     * @param signerRuleId  The identifier of the signing finance officer rule.
     */
    public SignatureInfo(Long dateTime, FinanceOfficerRuleId signerRuleId) {
        this.dateTime = dateTime;
        this.signerRuleId = signerRuleId;
    }

    /**
     * Returns the timestamp of the signature.
     */
    public Long getDateTime() {
        return dateTime;
    }

    /**
     * Returns the identifier of the signing finance officer rule.
     */
    public FinanceOfficerRuleId getSignerRuleId() {
        return signerRuleId;
    }

    /**
     * Two SignatureInfo objects are equal if both the date and signer ID match.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dateTime, signerRuleId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SignatureInfo other = (SignatureInfo) obj;
        return Objects.equals(dateTime, other.dateTime)
                && Objects.equals(signerRuleId, other.signerRuleId);
    }
}
