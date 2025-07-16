package com.pirshayan.controller.rest.signachtransferorder;


import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.handler.SignAchTransferOrderCommandHandler;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/rest/achtransferorder/sign")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Controller {
	PresenterImpl presenter;
	SignAchTransferOrderCommandHandler handler;

	public Controller(PresenterImpl presenter, SignAchTransferOrderCommandHandler handler) {
		this.presenter = presenter;
		this.handler = handler;
	}

	@POST
	public Response sign(RequestDto requestDto) {
		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(requestDto.signerRuleId(),
				requestDto.orderId());
		handler.handle(command, presenter);
		return presenter.getResponse();
	}

}
