package com.pirshayan.domain.model.financeofficerrule;

/**
 * Enumeration representing the hierarchical position of a finance officer within the organization.
 *
 * Each position is associated with a numeric rank. Higher rank values indicate greater authority.
 * This ranking can be used to resolve conflicts, determine approval chains, and enforce thresholds.
 */
enum Position {
    CEO(10),
    CFO(9),
    FINANCE_DIRECTOR_GENERAL(8),
    DEPUTY_FINANCE_DIRECTOR_GENERAL(7),
    REGIONAL_DIRECTOR_GENERAL(6),
    DEPUTY_REGIONAL_DIRECTOR_GENERAL(5),
    BRANCH_MANAGER(4),
    DEPUTY_BRANCH_FINANCE_MANAGER(3),
    PERMANENT_FINANCE_PERSONNEL(2),
    CONTRACT_FINANCE_PERSONNEL(1);

    // Numeric value representing the authority level of this position
    private final int rank;

    /**
     * Constructor that assigns the given rank to the position.
     *
     * @param rank integer value representing the hierarchical authority
     */
    Position(int rank){
        this.rank = rank;
    }

    /**
     * Returns the numeric rank associated with this position.
     *
     * @return integer value of the rank
     */
    public int getRank() {
        return rank;
    }
}
