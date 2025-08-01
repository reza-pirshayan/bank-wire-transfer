package com.pirshayan.application.presenter;

import com.pirshayan.domain.model.GeneralException;
import com.pirshayan.domain.model.InvalidDomainObjectException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSignerIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.FinanceOfficerRuleNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;

public interface SignAchTransferOrderPresenter {
	public void presentSuccess();

	public void presentFinanceOfficerRuleIsNotSignCandidateException(AchTransferOrderSignerIsNotSignCandidateException e);

	public void presentAchTransferOrderSigner1AndSigner2CannotBeTheSameException(
			AchTransferOrderSigner1AndSigner2CannotBeTheSameException e);

	public void presentFinanceOfficerNotPrivilegedToSignAsFirstSignerException(
			FinanceOfficerNotPrivilegedToSignAsFirstSignerException e);

	public void presentFinanceOfficerNotPrivilegedToSignAsSecondSignerException(
			FinanceOfficerNotPrivilegedToSignAsSecondSignerException e);

	public void presentAchTransferOrderNotFoundException(AchTransferOrderNotFoundException e);

	public void presentFinanceOfficerRuleNotFoundException(FinanceOfficerRuleNotFoundException e);

	public void presentInconsistentAchTransferOrderException(InconsistentAchTransferOrderException e);

	public void presentInvalidDomainObjectException(InvalidDomainObjectException e);

	public void presentGeneralException(GeneralException e);

}
