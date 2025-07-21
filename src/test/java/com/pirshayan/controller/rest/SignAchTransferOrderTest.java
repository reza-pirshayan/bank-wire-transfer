package com.pirshayan.controller.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTransactionalTestHelper;
import com.pirshayan.controller.rest.signachtransferorder.RequestDto;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class SignAchTransferOrderTest {
	@Inject
	AchTransferOrderAggregateTransactionalTestHelper testHelper;

	@Test
	void sign_ach_transfer_order__should_be_successful() {
		// Arrange
		Long signerRuleId = 1113005254L;
		String orderId = "2025071200003";
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(orderId);
		testHelper.deleteAchTransferOrder(achTransferOrderId);
		testHelper.createPendingFirstSignatureAchTransferOrder(achTransferOrderId);
		testHelper.clearPersistenceContext();
		RequestDto requestDto = new RequestDto(signerRuleId, orderId);

		// Act
		var response = given().contentType("application/json").body(requestDto).when()
				.post("/rest/achtransferorder/sign").then().extract().response();

		// Assert
		assertEquals(200, response.getStatusCode());

	}
}
