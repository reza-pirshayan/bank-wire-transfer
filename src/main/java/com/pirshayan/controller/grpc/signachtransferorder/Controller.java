package com.pirshayan.controller.grpc.signachtransferorder;

import com.pirshayan.SignAchTransferOrder;
import com.pirshayan.SignAchTransferOrderRequest;
import com.pirshayan.SignAchTransferOrderResponse;
import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.handler.SignAchTransferOrderCommandHandler;

import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

@GrpcService
public class Controller implements SignAchTransferOrder {
	private PresenterImpl presenter;
	private SignAchTransferOrderCommandHandler handler;

	public Controller(PresenterImpl presenter, SignAchTransferOrderCommandHandler handler) {
		this.presenter = presenter;
		this.handler = handler;
	}

	@Override
	@Blocking
	public Uni<SignAchTransferOrderResponse> signAchTransferOrderService(SignAchTransferOrderRequest request) {
		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(request.getSignerRuleId(),
				request.getOrderId());
		handler.handle(command, presenter);
		return presenter.getResponse();
	}

}
