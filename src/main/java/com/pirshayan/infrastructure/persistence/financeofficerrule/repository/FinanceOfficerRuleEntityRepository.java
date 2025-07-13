package com.pirshayan.infrastructure.persistence.financeofficerrule.repository;

import com.pirshayan.infrastructure.persistence.financeofficerrule.entity.FinanceOfficerRuleEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FinanceOfficerRuleEntityRepository implements PanacheRepository<FinanceOfficerRuleEntity> {
	
}
