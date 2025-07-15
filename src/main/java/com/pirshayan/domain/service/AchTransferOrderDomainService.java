package com.pirshayan.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;
import com.pirshayan.domain.service.exception.SecondSignersRankLowerThanFirstSignersRankException;

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

			if (achTransferOrder.getSecondSignerCandidateIds().isEmpty()) {
				throw new IllegalStateException(String.format(
						"ACH transfer order with ID [ %s ] and state [ %s ] cannot have null second signer candidate id list.",
						achTransferOrder.getAchTransferOrderId().getId(), achTransferOrder.getStatusString()));
			}

			List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = refineSecondSignerCandidates(signerRule,
					achTransferOrder.getSecondSignerCandidateIds());

			return achTransferOrder.signAsFirst(signerRule.getFinanceOfficerRuleId(), System.currentTimeMillis(),
					refinedSecondSignerCandidateIds);

		} else if (achTransferOrder.isPendingSecondSignature()) {

			signerRule.ensureSufficientPrivilegesForSecondSignature(achTransferOrder.getTransferAmount());

			if (achTransferOrder.getFirstSignatureDateTime().isEmpty()
					|| achTransferOrder.getFirstSignerRuleId().isEmpty()) {
				throw new IllegalStateException(String.format(
						"ACH transfer order with ID [ %s ] and status [ %s ] must not have null first signature info.",
						achTransferOrder.getAchTransferOrderId(), achTransferOrder.getStatusString()));
			}

			FinanceOfficerRuleAggregateRoot firstSignerRule = financeOfficerRuleAggregateRepository
					.findById(achTransferOrder.getFirstSignerRuleId().get());

			if (signerRule.getRank() < firstSignerRule.getRank()) {
				throw new SecondSignersRankLowerThanFirstSignersRankException(achTransferOrder, signerRule,
						firstSignerRule);
			}

			return achTransferOrder.signAsSecond(signerRule.getFinanceOfficerRuleId(), System.currentTimeMillis());

		} else {
			throw new IllegalStateException(
					String.format("ACH transfer order with ID [ %s ] and status [ %s ] cannot be signed.",
							achTransferOrder.getAchTransferOrderId().getId(), achTransferOrder.getStatusString()));
		}
	}

	private List<FinanceOfficerRuleId> refineSecondSignerCandidates(FinanceOfficerRuleAggregateRoot signerRule,
			List<FinanceOfficerRuleId> secondSignerCandidateIds) {

		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = new ArrayList<>();

		for (FinanceOfficerRuleId secondSignerCandidateId : secondSignerCandidateIds) {
			FinanceOfficerRuleAggregateRoot secondSignerCandidate = financeOfficerRuleAggregateRepository
					.findById(secondSignerCandidateId);

			if (secondSignerCandidate.getRank() >= signerRule.getRank() && !secondSignerCandidate.equals(signerRule)) {
				refinedSecondSignerCandidateIds.add(secondSignerCandidateId);
			}
		}
		return refinedSecondSignerCandidateIds;
	}
}
