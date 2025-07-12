package com.pirshayan.infrastructure.persistence.financeofficerrule.repository;

import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FinanceOfficerRuleRepositoryImpl implements FinanceOfficerRuleAggregateRepository {

	@Override
	public FinanceOfficerRuleAggregateRoot findById(FinanceOfficerRuleId financeOfficerRuleId) {
		// TODO Auto-generated method stub
		return null;
	}

}
