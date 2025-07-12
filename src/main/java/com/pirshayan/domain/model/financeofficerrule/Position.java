package com.pirshayan.domain.model.financeofficerrule;

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

    private int rank;

    Position(int rank){
        this.rank = rank;
    }
    public int getRank() {
        return rank;
    }
}
