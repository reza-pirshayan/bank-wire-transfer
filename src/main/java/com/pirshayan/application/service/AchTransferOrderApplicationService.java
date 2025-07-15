package com.pirshayan.application.service;

import com.pirshayan.application.command.CancelAchTransferOrderCommand;
import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;
import com.pirshayan.domain.service.AchTransferOrderDomainService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AchTransferOrderApplicationService {
	private final FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository;
	private final AchTransferOrderAggregateRepository achTransferOrderAggregateRepository;
	private final AchTransferOrderDomainService achTransferOrderDomainService;

	public AchTransferOrderApplicationService(
			FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository,
			AchTransferOrderAggregateRepository achTransferOrderAggregateRepository,
			AchTransferOrderDomainService achTransferOrderDomainService) {
		
		this.financeOfficerRuleAggregateRepository = financeOfficerRuleAggregateRepository;
		this.achTransferOrderAggregateRepository = achTransferOrderAggregateRepository;
		this.achTransferOrderDomainService = achTransferOrderDomainService;
	}

	public void sign(SignAchTransferOrderCommand command) {
		// Create a value object for the signer's rule ID based on the command input
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(command.getSignerRuleId());

		// Load the signer’s rule aggregate from the repository (includes their rank and
		// privileges)
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(signerRuleId);
		// Create a value object for the current ACH transfer order ID

		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(command.getAchTransferOrderId());

		// Load the current ACH transfer order aggregate from the repository
		AchTransferOrderAggregateRoot achTransferOrder = achTransferOrderAggregateRepository
				.findById(achTransferOrderId);

		// Delegate to domain service to perform business logic and sign the transfer
		// order
		AchTransferOrderAggregateRoot signedAchTransferOrder = achTransferOrderDomainService.sign(signerRule,
				achTransferOrder);

		// Persist the updated (signed) ACH transfer order back to the repository
		achTransferOrderAggregateRepository.update(signedAchTransferOrder);
	}
	
	public void cancel(CancelAchTransferOrderCommand command) {
		 // TODO do it as an exercise!
	}
}
