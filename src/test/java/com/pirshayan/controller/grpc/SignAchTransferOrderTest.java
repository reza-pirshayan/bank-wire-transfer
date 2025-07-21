package com.pirshayan.controller.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTransactionalTestHelper;
import com.pirshayan.SignAchTransferOrderGrpc.SignAchTransferOrderBlockingStub;
import com.pirshayan.SignAchTransferOrderRequest;
import com.pirshayan.SignAchTransferOrderResponse;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class SignAchTransferOrderTest {
	@Inject
	AchTransferOrderAggregateTransactionalTestHelper testHelper;

	@GrpcClient("sign-ach-transfer-order-list")
	SignAchTransferOrderBlockingStub sut;

	@Test
	void sign_ach_transfer_order__should_be_successful() {
		// Arrange
		Long signerRuleId = 1113005254L;
		String orderId = "2025071200004";
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(orderId);
		testHelper.deleteAchTransferOrder(achTransferOrderId);
		testHelper.createPendingFirstSignatureAchTransferOrder(achTransferOrderId);
		testHelper.clearPersistenceContext();

		SignAchTransferOrderRequest request = SignAchTransferOrderRequest.newBuilder().setSignerRuleId(signerRuleId)
				.setOrderId(orderId).build();

		// Act
		SignAchTransferOrderResponse response = sut.signAchTransferOrderService(request);

		// Assert
		assertEquals("200", response.getCode());

	}
}
