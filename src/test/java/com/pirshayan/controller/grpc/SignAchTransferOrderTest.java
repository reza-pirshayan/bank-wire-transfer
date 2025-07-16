package com.pirshayan.controller.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTestHelper;
import com.pirshayan.SignAchTransferOrderGrpc.SignAchTransferOrderBlockingStub;
import com.pirshayan.SignAchTransferOrderRequest;
import com.pirshayan.SignAchTransferOrderResponse;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class SignAchTransferOrderTest {
	@Inject
	AchTransferOrderAggregateRepository achTransferOrderAggregateRepository;

	@GrpcClient("sign-ach-transfer-order-list")
	SignAchTransferOrderBlockingStub sut;

	@Test
	void sign_ach_transfer_order__should_be_successful() {
		// Arrange
		Long signerRuleId = 1113005254L;
		String orderId = "2025071200004";
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(orderId);
		achTransferOrderAggregateRepository.deleteById(achTransferOrderId);
		AchTransferOrderAggregateRoot achTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(orderId);
		achTransferOrderAggregateRepository.create(achTransferOrder);
		achTransferOrderAggregateRepository.clearPersistenceContext();

		SignAchTransferOrderRequest request = SignAchTransferOrderRequest.newBuilder().setSignerRuleId(signerRuleId)
				.setOrderId(orderId).build();

		// Act
		SignAchTransferOrderResponse response = sut.signAchTransferOrderService(request);

		// Assert
		assertEquals("200", response.getCode());

	}
}
