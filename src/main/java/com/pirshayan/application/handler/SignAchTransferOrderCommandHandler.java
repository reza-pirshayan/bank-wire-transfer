package com.pirshayan.application.handler;

import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.presenter.SignAchTransferOrderPresenter;
import com.pirshayan.application.service.AchTransferOrderApplicationService;
import com.pirshayan.domain.model.GeneralException;
import com.pirshayan.domain.model.InvalidDomainObjectException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSignerIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.FinanceOfficerRuleNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SignAchTransferOrderCommandHandler {
	private final AchTransferOrderApplicationService achTransferOrderApplicationService;

	public SignAchTransferOrderCommandHandler(AchTransferOrderApplicationService achTransferOrderApplicationService) {
		this.achTransferOrderApplicationService = achTransferOrderApplicationService;
	}

	public void handle(SignAchTransferOrderCommand command, SignAchTransferOrderPresenter presenter) {
		try {
			achTransferOrderApplicationService.sign(command);
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
