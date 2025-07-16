package com.pirshayan.controller.grpc.signachtransferorder;

import com.pirshayan.SignAchTransferOrderResponse;
import com.pirshayan.application.presenter.SignAchTransferOrderPresenter;
import com.pirshayan.domain.exception.GeneralException;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.FinanceOfficerRuleIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.repository.exception.AchTransferOrderNotFoundException;
import com.pirshayan.domain.repository.exception.FinanceOfficerRuleNotFoundException;
import com.pirshayan.domain.repository.exception.InconsistentAchTransferOrderException;
import com.pirshayan.domain.service.exception.SecondSignersRankLowerThanFirstSignersRankException;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PresenterImpl implements SignAchTransferOrderPresenter {
	private Uni<SignAchTransferOrderResponse> response;

	public Uni<SignAchTransferOrderResponse> getResponse() {
		return response;
	}

	@Override
	public void presentSuccess() {
		response = Uni.createFrom()
				.item(SignAchTransferOrderResponse.newBuilder().setCode("200").setMessage("successful").build());

	}

	@Override
	public void presentFinanceOfficerRuleIsNotSignCandidateException(FinanceOfficerRuleIsNotSignCandidateException e) {
		response = Uni.createFrom().failure(handleException(Status.INVALID_ARGUMENT, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentAchTransferOrderSigner1AndSigner2CannotBeTheSameException(
			AchTransferOrderSigner1AndSigner2CannotBeTheSameException e) {
		response = Uni.createFrom().failure(handleException(Status.INVALID_ARGUMENT, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentSecondSignersRankLowerThanFirstSignersRankException(
			SecondSignersRankLowerThanFirstSignersRankException e) {
		response = Uni.createFrom().failure(handleException(Status.PERMISSION_DENIED, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentFinanceOfficerNotPrivilegedToSignAsFirstSignerException(
			FinanceOfficerNotPrivilegedToSignAsFirstSignerException e) {
		response = Uni.createFrom().failure(handleException(Status.PERMISSION_DENIED, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentFinanceOfficerNotPrivilegedToSignAsSecondSignerException(
			FinanceOfficerNotPrivilegedToSignAsSecondSignerException e) {
		response = Uni.createFrom().failure(handleException(Status.PERMISSION_DENIED, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentAchTransferOrderNotFoundException(AchTransferOrderNotFoundException e) {
		response = Uni.createFrom().failure(handleException(Status.NOT_FOUND, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentFinanceOfficerRuleNotFoundException(FinanceOfficerRuleNotFoundException e) {
		response = Uni.createFrom().failure(handleException(Status.NOT_FOUND, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentInconsistentAchTransferOrderException(InconsistentAchTransferOrderException e) {
		response = Uni.createFrom().failure(handleException(Status.INTERNAL, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentGeneralException(GeneralException e) {
		response = Uni.createFrom().failure(handleException(Status.INTERNAL, e.getMessage(), e.getCode()));

	}

	@Override
	public void presentRuntimeException(RuntimeException e) {
		response = Uni.createFrom().failure(handleException(Status.INTERNAL, e.getMessage(), "-1"));

	}

	/**
	 * Helper method to handle exceptions.
	 */
	private StatusRuntimeException handleException(Status status, String message, String code) {
		Metadata metadata = new Metadata();
		Metadata.Key<String> codeKey = Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER);
		Metadata.Key<String> messageKey = Metadata.Key.of("error-message", Metadata.ASCII_STRING_MARSHALLER);

		metadata.put(codeKey, code);
		metadata.put(messageKey, message);

		return status.asRuntimeException(metadata);
	}
}
