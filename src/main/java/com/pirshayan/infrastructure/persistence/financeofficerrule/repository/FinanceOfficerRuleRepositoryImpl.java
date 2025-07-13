package com.pirshayan.infrastructure.persistence.financeofficerrule.repository;

import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;
import com.pirshayan.domain.repository.exception.FinanceOfficerRuleNotFoundException;
import com.pirshayan.infrastructure.persistence.financeofficerrule.entity.FinanceOfficerRuleEntity;
import com.pirshayan.infrastructure.persistence.financeofficerrule.mapper.FinanceOfficerRuleMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FinanceOfficerRuleRepositoryImpl implements FinanceOfficerRuleAggregateRepository {
	private final FinanceOfficerRuleEntityRepository financeOfficerRuleEntityRepository;

	public FinanceOfficerRuleRepositoryImpl(FinanceOfficerRuleEntityRepository financeOfficerRuleEntityRepository) {
		this.financeOfficerRuleEntityRepository = financeOfficerRuleEntityRepository;
	}

	@Override
	public FinanceOfficerRuleAggregateRoot findById(FinanceOfficerRuleId financeOfficerRuleId) {
		FinanceOfficerRuleEntity financeOfficerRuleEntity = financeOfficerRuleEntityRepository
				.findByIdOptional(financeOfficerRuleId.getId())
				.orElseThrow(() -> new FinanceOfficerRuleNotFoundException(financeOfficerRuleId));
		
		return FinanceOfficerRuleMapper.toModel(financeOfficerRuleEntity);
	}

}
