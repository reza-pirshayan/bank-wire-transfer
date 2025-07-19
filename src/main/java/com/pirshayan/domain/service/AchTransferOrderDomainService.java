package com.pirshayan.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AchTransferOrderDomainService {
	private final FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository;

	public AchTransferOrderDomainService(FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository) {
		this.financeOfficerRuleAggregateRepository = financeOfficerRuleAggregateRepository;
	}

	public AchTransferOrderAggregateRoot sign(FinanceOfficerRuleAggregateRoot signerRule,
			AchTransferOrderAggregateRoot achTransferOrder) {
		if (achTransferOrder.isPendingFirstSignature()) {

			signerRule.ensureSufficientPrivilegesForFirstSignature(achTransferOrder.getTransferAmount());

			if (achTransferOrder.getSecondSignerCandidateRuleIds().isEmpty()) {
				throw new IllegalStateException(String.format(
						"ACH transfer order with ID [ %s ] and state [ %s ] cannot have null second signer candidate id list.",
						achTransferOrder.getAchTransferOrderId().getId(), achTransferOrder.getStatusString()));
			}

			List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = refineSecondSignerCandidateRuleIds(signerRule,
					achTransferOrder.getSecondSignerCandidateRuleIds());

			return achTransferOrder.signAsFirst(signerRule.getFinanceOfficerRuleId(), System.currentTimeMillis(),
					refinedSecondSignerCandidateIds);

		} else if (achTransferOrder.isPendingSecondSignature()) {

			signerRule.ensureSufficientPrivilegesForSecondSignature(achTransferOrder.getTransferAmount());

			return achTransferOrder.signAsSecond(signerRule.getFinanceOfficerRuleId(), System.currentTimeMillis());

		} else {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and status [ %s ] cannot be signed.",
							achTransferOrder.getAchTransferOrderId().getId(), achTransferOrder.getStatusString()));
		}
	}

	private List<FinanceOfficerRuleId> refineSecondSignerCandidateRuleIds(FinanceOfficerRuleAggregateRoot signerRule,
			List<FinanceOfficerRuleId> secondSignerCandidateًRuleIds) {

		List<FinanceOfficerRuleId> refinedSecondSignerCandidateRuleIds = new ArrayList<>();

		for (FinanceOfficerRuleId secondSignerCandidateRuleId : secondSignerCandidateًRuleIds) {
			FinanceOfficerRuleAggregateRoot secondSignerCandidateRule = financeOfficerRuleAggregateRepository
					.findById(secondSignerCandidateRuleId);

			if (secondSignerCandidateRule.getRank() >= signerRule.getRank()
					&& !secondSignerCandidateRule.equals(signerRule)) {
				refinedSecondSignerCandidateRuleIds.add(secondSignerCandidateRuleId);
			}
		}
		return refinedSecondSignerCandidateRuleIds;
	}
}
