package com.pirshayan.controller.rest.signachtransferorder;

import com.pirshayan.application.presenter.SignAchTransferOrderPresenter;
import com.pirshayan.domain.model.exception.GeneralException;
import com.pirshayan.domain.model.exception.InvalidDomainObjectException;
import com.pirshayan.domain.model.exception.achtransferorder.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.exception.achtransferorder.FinanceOfficerRuleIsNotSignCandidateException;
import com.pirshayan.domain.model.exception.financeofficer.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.exception.financeofficer.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.FinanceOfficerRuleNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class PresenterImpl implements SignAchTransferOrderPresenter {
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	@Override
	public void presentSuccess() {

		response = Response.status(Response.Status.OK).entity(new ResponseDto(String.valueOf(0), "successful")).build();

	}

	@Override
	public void presentFinanceOfficerRuleIsNotSignCandidateException(FinanceOfficerRuleIsNotSignCandidateException e) {

		response = Response.status(Response.Status.BAD_REQUEST).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentAchTransferOrderSigner1AndSigner2CannotBeTheSameException(
			AchTransferOrderSigner1AndSigner2CannotBeTheSameException e) {

		response = Response.status(Response.Status.BAD_REQUEST).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentFinanceOfficerNotPrivilegedToSignAsFirstSignerException(
			FinanceOfficerNotPrivilegedToSignAsFirstSignerException e) {

		response = Response.status(Response.Status.UNAUTHORIZED).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentFinanceOfficerNotPrivilegedToSignAsSecondSignerException(
			FinanceOfficerNotPrivilegedToSignAsSecondSignerException e) {

		response = Response.status(Response.Status.UNAUTHORIZED).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentAchTransferOrderNotFoundException(AchTransferOrderNotFoundException e) {

		response = Response.status(Response.Status.NOT_FOUND).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentFinanceOfficerRuleNotFoundException(FinanceOfficerRuleNotFoundException e) {

		response = Response.status(Response.Status.NOT_FOUND).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentInconsistentAchTransferOrderException(InconsistentAchTransferOrderException e) {

		response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ResponseDto(e.getCode(), e.getMessage())).build();

	}

	@Override
	public void presentInvalidDomainObjectException(InvalidDomainObjectException e) {
		response = Response.status(Response.Status.BAD_REQUEST).entity(new ResponseDto(e.getCode(), e.getMessage()))
				.build();

	}

	@Override
	public void presentGeneralException(GeneralException e) {

		response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ResponseDto(e.getCode(), e.getMessage())).build();

	}

	@Override
	public void presentRuntimeException(RuntimeException e) {
		response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ResponseDto(String.valueOf(-1), e.getMessage())).build();

	}

}
