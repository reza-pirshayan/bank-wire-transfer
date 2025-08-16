package com.pirshayan;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;
import com.pirshayan.domain.service.AchTransferOrderDomainService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AchTransferOrderAggregateTransactionalTestHelper {
	AchTransferOrderAggregateRepository achTransferOrderAggregateRepository;
	AchTransferOrderDomainService achTransferOrderDomainService;
	FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository;

	public AchTransferOrderAggregateTransactionalTestHelper(
			AchTransferOrderAggregateRepository achTransferOrderAggregateRepository,
			AchTransferOrderDomainService achTransferOrderDomainService,
			FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository) {
		this.achTransferOrderAggregateRepository = achTransferOrderAggregateRepository;
		this.achTransferOrderDomainService = achTransferOrderDomainService;
		this.financeOfficerRuleAggregateRepository = financeOfficerRuleAggregateRepository;
	}

	@Transactional
	public AchTransferOrderAggregateRoot createPendingFirstSignatureAchTransferOrder(
			AchTransferOrderId achTransferOrderId) {

		AchTransferOrderAggregateRoot achTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(achTransferOrderId.getId());
		achTransferOrderAggregateRepository.create(achTransferOrder);
		return achTransferOrder;
	}

	@Transactional
	public void createPendingSecondSignatureAchTransferOrder(AchTransferOrderId achTransferOrderId) {

		AchTransferOrderAggregateRoot pendingFirstSignatureAchTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(achTransferOrderId.getId());
		achTransferOrderAggregateRepository.create(pendingFirstSignatureAchTransferOrder);
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(1113005254L);
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(signerRuleId);
		AchTransferOrderAggregateRoot pendingSecondSignatureAchTransferOrder = achTransferOrderDomainService
				.sign(signerRule, pendingFirstSignatureAchTransferOrder);
		achTransferOrderAggregateRepository.update(pendingSecondSignatureAchTransferOrder);
	}

	@Transactional
	public void deleteAchTransferOrder(AchTransferOrderId achTransferOrderId) {
		achTransferOrderAggregateRepository.deleteById(achTransferOrderId);
	}

	public AchTransferOrderAggregateRoot HydrateAchTransferOrder(AchTransferOrderId achTransferOrderId) {
		return achTransferOrderAggregateRepository.findById(achTransferOrderId);
	}

	public void clearPersistenceContext() {
		achTransferOrderAggregateRepository.clearPersistenceContext();
	}
}
