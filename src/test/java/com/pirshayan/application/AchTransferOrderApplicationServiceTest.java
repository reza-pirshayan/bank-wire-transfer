package com.pirshayan.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pirshayan.AchTransferOrderAggregateTransactionalTestHelper;
import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.handler.SignAchTransferOrderCommandHandler;
import com.pirshayan.application.presenter.SignAchTransferOrderPresenter;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class AchTransferOrderApplicationServiceTest {
	@Inject
	AchTransferOrderAggregateTransactionalTestHelper testHelper;

	@Inject
	SignAchTransferOrderCommandHandler sut;

	@Mock
	SignAchTransferOrderPresenter presenter;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

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
		doNothing().when(presenter).presentSuccess();

		// Act
		sut.handle(command, presenter);

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
		testHelper.createPendingSecondSignatureAchTransferOrder(achTransferOrderId);
		testHelper.clearPersistenceContext();
		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(commandSecondSignerRuleId,
				commandOrderId);
		doNothing().when(presenter).presentSuccess();
		
		// Act
		sut.handle(command, presenter);

		// Assert
		testHelper.clearPersistenceContext();
		AchTransferOrderId achTransferOrderAggregateId = new AchTransferOrderId(commandOrderId);
		AchTransferOrderAggregateRoot signedAchTransferOrder = testHelper
				.HydrateAchTransferOrder(achTransferOrderAggregateId);
		assertTrue(signedAchTransferOrder.isPendingBankDispatch());
		assertTrue(signedAchTransferOrder.getSecondSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getSecondSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getSecondSignerRuleId().get().getId(), commandSecondSignerRuleId);
		assertTrue(signedAchTransferOrder.getSecondSignerCandidateRuleIds().isEmpty());
	}
}
