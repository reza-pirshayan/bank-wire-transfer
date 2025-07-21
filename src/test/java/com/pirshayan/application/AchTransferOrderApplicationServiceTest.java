package com.pirshayan.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTransactionalTestHelper;
import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.service.AchTransferOrderApplicationService;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class AchTransferOrderApplicationServiceTest {
	@Inject
	AchTransferOrderAggregateTransactionalTestHelper testHelper;

	@Inject
	AchTransferOrderApplicationService sut;

	@Test
	void sign_ach_transfer_order_with_status_pending_first_signature__should_be_successful() {
		// Arrange
		Long commandSignerRuleId = 1113005254L;
		String commandOrderId = "2025071200001";
		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(commandSignerRuleId, commandOrderId);
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(commandOrderId);
		testHelper.deleteAchTransferOrder(achTransferOrderId);
		testHelper.createPendingFirstSignatureAchTransferOrder(achTransferOrderId);
		testHelper.clearPersistenceContext();
		
		// Act
		sut.sign(command);

		// Assert
		AchTransferOrderAggregateRoot signedAchTransferOrder = testHelper.HydrateAchTransferOrder(achTransferOrderId);
		assertTrue(signedAchTransferOrder.isPendingSecondSignature());
		assertTrue(signedAchTransferOrder.getFirstSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getFirstSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getFirstSignerRuleId().get().getId(), commandSignerRuleId);
		assertTrue(signedAchTransferOrder.getFirstSignerCandidateRuleIds().isEmpty());
		assertFalse(signedAchTransferOrder.getSecondSignerCandidateRuleIds().isEmpty());
	}

	@Test
	void sign_ach_transfer_order_with_status_pending_second_signature__should_be_successful() {
		// Arrange
		Long commandSecondSignerRuleId = 1112504840L;
		String commandOrderId = "2025071200002";
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(commandOrderId);
		testHelper.deleteAchTransferOrder(achTransferOrderId);
		testHelper.createPendingFirstSignatureAchTransferOrder(achTransferOrderId);
		testHelper.clearPersistenceContext();
		testHelper.createPendingSecondSignatureAchTransferOrder(achTransferOrderId);
		testHelper.clearPersistenceContext();
		SignAchTransferOrderCommand secondSigncommand = new SignAchTransferOrderCommand(commandSecondSignerRuleId, commandOrderId);

		// Act
		sut.sign(secondSigncommand);

		// Assert
		testHelper.clearPersistenceContext();
		AchTransferOrderId achTransferOrderAggregateId = new AchTransferOrderId(commandOrderId);
		AchTransferOrderAggregateRoot signedAchTransferOrder = testHelper.HydrateAchTransferOrder(achTransferOrderAggregateId);
		assertTrue(signedAchTransferOrder.isPendingSend());
		assertTrue(signedAchTransferOrder.getSecondSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getSecondSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getSecondSignerRuleId().get().getId(), commandSecondSignerRuleId);
		assertTrue(signedAchTransferOrder.getSecondSignerCandidateRuleIds().isEmpty());
	}
}
