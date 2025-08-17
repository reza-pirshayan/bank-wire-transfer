package com.pirshayan.application.handler;

import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.presenter.SignAchTransferOrderPresenter;
import com.pirshayan.domain.model.GeneralException;
import com.pirshayan.domain.model.InvalidDomainObjectException;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSignerIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.FinanceOfficerRuleNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;
import com.pirshayan.domain.service.AchTransferOrderDomainService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class SignAchTransferOrderCommandHandler {
	private final FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository;
	private final AchTransferOrderAggregateRepository achTransferOrderAggregateRepository;
	private final AchTransferOrderDomainService achTransferOrderDomainService;

	public SignAchTransferOrderCommandHandler(
			FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository,
			AchTransferOrderAggregateRepository achTransferOrderAggregateRepository,
			AchTransferOrderDomainService achTransferOrderDomainService) {
		this.financeOfficerRuleAggregateRepository = financeOfficerRuleAggregateRepository;
		this.achTransferOrderAggregateRepository = achTransferOrderAggregateRepository;
		this.achTransferOrderDomainService = achTransferOrderDomainService;
	}

	public void handle(SignAchTransferOrderCommand command, SignAchTransferOrderPresenter presenter) {
		try {

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
			presenter.presentSuccess();
		} catch (AchTransferOrderSignerIsNotSignCandidateException e) {
			presenter.presentFinanceOfficerRuleIsNotSignCandidateException(e);
		} catch (AchTransferOrderSigner1AndSigner2CannotBeTheSameException e) {
			presenter.presentAchTransferOrderSigner1AndSigner2CannotBeTheSameException(e);
		} catch (FinanceOfficerNotPrivilegedToSignAsFirstSignerException e) {
			presenter.presentFinanceOfficerNotPrivilegedToSignAsFirstSignerException(e);
		} catch (FinanceOfficerNotPrivilegedToSignAsSecondSignerException e) {
			presenter.presentFinanceOfficerNotPrivilegedToSignAsSecondSignerException(e);
		} catch (AchTransferOrderNotFoundException e) {
			presenter.presentAchTransferOrderNotFoundException(e);
		} catch (FinanceOfficerRuleNotFoundException e) {
			presenter.presentFinanceOfficerRuleNotFoundException(e);
		} catch (InconsistentAchTransferOrderException e) {
			presenter.presentInconsistentAchTransferOrderException(e);
		} catch (InvalidDomainObjectException e) {
			presenter.presentInvalidDomainObjectException(e);
		} catch (GeneralException e) {
			presenter.presentGeneralException(e);
		}
	}

}
