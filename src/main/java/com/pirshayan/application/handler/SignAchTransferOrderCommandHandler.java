package com.pirshayan.application.handler;

import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.presenter.SignAchTransferOrderPresenter;
import com.pirshayan.application.service.AchTransferOrderApplicationService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SignAchTransferOrderCommandHandler  {
	private final AchTransferOrderApplicationService achTransferOrderApplicationService;

	public SignAchTransferOrderCommandHandler(AchTransferOrderApplicationService achTransferOrderApplicationService) {
		this.achTransferOrderApplicationService = achTransferOrderApplicationService;
	}

	public void handle(SignAchTransferOrderCommand command, SignAchTransferOrderPresenter presenter) {
		try {
			achTransferOrderApplicationService.sign(command);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


}
