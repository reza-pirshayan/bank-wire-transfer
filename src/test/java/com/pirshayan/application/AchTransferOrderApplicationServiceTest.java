package com.pirshayan.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
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
		String commandAchTransferOrderId = "2025071200001";
		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(commandSignerRuleId, commandAchTransferOrderId);
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId(commandAchTransferOrderId);
		achTransferOrderAggregateRepository.deleteById(achTransferOrderId);
		
		AchTransferOrderAggregateRoot achTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder();
		achTransferOrderAggregateRepository.create(achTransferOrder);

		
		// Act
		sut.sign(command);
		
		// Assert
		achTransferOrderAggregateRepository.clearPersistenceContext();
		AchTransferOrderAggregateRoot signedAchTransferOrder =  achTransferOrderAggregateRepository.findById(achTransferOrderId);
		assertTrue(signedAchTransferOrder.isPendingSecondSignature());
		assertTrue(signedAchTransferOrder.getFirstSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getFirstSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getFirstSignerRuleId().get().getId(), commandSignerRuleId);
		assertTrue(signedAchTransferOrder.getFirstSignerCandidateIds().isEmpty());
		assertFalse(signedAchTransferOrder.getSecondSignerCandidateIds().isEmpty());
	}
	
	@Test
	@Disabled
	void sign_ach_transfer_order_with_status_pending_second_signature__should_be_successful() {
		// Arrange
//		AchTransferOrderAggregateRoot achTransferOrder = AchTransferOrderAggregateTestHelper
//				.buildPendingFirstSignatureAchTransferOrder();
//		achTransferOrderAggregateRepository.create(achTransferOrder);
		Long signerRuleId = 1112504840L;
		String achTransferOrderId = "2025071200001";
//		SignAchTransferOrderCommand command = new SignAchTransferOrderCommand(signerRuleId, achTransferOrderId);
//		
//		// Act
//		sut.sign(command);
		
		// Assert
		achTransferOrderAggregateRepository.clearPersistenceContext();
		AchTransferOrderId achTransferOrderAggregateId = new AchTransferOrderId(achTransferOrderId);
		AchTransferOrderAggregateRoot signedAchTransferOrder =  achTransferOrderAggregateRepository.findById(achTransferOrderAggregateId);
		assertTrue(signedAchTransferOrder.isPendingSend());
		assertTrue(signedAchTransferOrder.getSecondSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getSecondSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getSecondSignerRuleId().get().getId(), signerRuleId);
		assertTrue(signedAchTransferOrder.getSecondSignerCandidateIds().isEmpty());
	}
}
