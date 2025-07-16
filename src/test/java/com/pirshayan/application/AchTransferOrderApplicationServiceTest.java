package com.pirshayan.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTestHelper;
import com.pirshayan.application.command.SignAchTransferOrderCommand;
import com.pirshayan.application.service.AchTransferOrderApplicationService;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class AchTransferOrderApplicationServiceTest {
	@Inject
	AchTransferOrderAggregateRepository achTransferOrderAggregateRepository;

	@Inject
	AchTransferOrderApplicationService sut;

	@Test
	void sign_ach_transfer_order_with_status_pending_first_signature__should_be_successful() {
		// Arrange
		Long commandSignerRuleId = 1113005254L;
		String commandOrderId = "2025071200001";
		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(commandSignerRuleId, commandOrderId);
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(commandOrderId);
		achTransferOrderAggregateRepository.deleteById(achTransferOrderId);

		AchTransferOrderAggregateRoot achTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(commandOrderId);
		achTransferOrderAggregateRepository.create(achTransferOrder);
		
		achTransferOrderAggregateRepository.clearPersistenceContext();
		
		// Act
		sut.sign(command);

		// Assert
		achTransferOrderAggregateRepository.clearPersistenceContext();
		AchTransferOrderAggregateRoot signedAchTransferOrder = achTransferOrderAggregateRepository
				.findById(achTransferOrderId);
		assertTrue(signedAchTransferOrder.isPendingSecondSignature());
		assertTrue(signedAchTransferOrder.getFirstSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getFirstSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getFirstSignerRuleId().get().getId(), commandSignerRuleId);
		assertTrue(signedAchTransferOrder.getFirstSignerCandidateIds().isEmpty());
		assertFalse(signedAchTransferOrder.getSecondSignerCandidateIds().isEmpty());
	}

	@Test
	void sign_ach_transfer_order_with_status_pending_second_signature__should_be_successful() {
		// Arrange
		Long commandFirstSignerRuleId = 1113005254L;
		Long commandSecondSignerRuleId = 1112504840L;
		String commandOrderId = "2025071200002";
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(commandOrderId);
		
		achTransferOrderAggregateRepository.deleteById(achTransferOrderId);
		
		AchTransferOrderAggregateRoot achTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(commandOrderId);
		achTransferOrderAggregateRepository.create(achTransferOrder);
		
		SignAchTransferOrderCommand firstSigncommand = new SignAchTransferOrderCommand(commandFirstSignerRuleId, commandOrderId);
		
		sut.sign(firstSigncommand);
		
		achTransferOrderAggregateRepository.clearPersistenceContext();
		
		SignAchTransferOrderCommand secondSigncommand = new SignAchTransferOrderCommand(commandSecondSignerRuleId, commandOrderId);

		// Act
		sut.sign(secondSigncommand);

		// Assert
		achTransferOrderAggregateRepository.clearPersistenceContext();
		AchTransferOrderId achTransferOrderAggregateId = new AchTransferOrderId(commandOrderId);
		AchTransferOrderAggregateRoot signedAchTransferOrder = achTransferOrderAggregateRepository
				.findById(achTransferOrderAggregateId);
		assertTrue(signedAchTransferOrder.isPendingSend());
		assertTrue(signedAchTransferOrder.getSecondSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getSecondSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getSecondSignerRuleId().get().getId(), commandSecondSignerRuleId);
		assertTrue(signedAchTransferOrder.getSecondSignerCandidateIds().isEmpty());
	}
}
