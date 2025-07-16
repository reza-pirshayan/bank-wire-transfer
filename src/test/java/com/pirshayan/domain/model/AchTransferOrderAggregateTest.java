package com.pirshayan.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTestHelper;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.achtransferorder.exception.FinanceOfficerRuleIsNotSignCandidateException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

public class AchTransferOrderAggregateTest {
	@Test
	void sign_as_first_ach_transfer_order_should_be_successful() {
		// Arrange
		String orderId = "025071200001"; 
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(orderId);
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(1113005254L);
		Long signDateTime = System.currentTimeMillis();
		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = Arrays
				.asList(new FinanceOfficerRuleId(1113091628L), new FinanceOfficerRuleId(1112504840L));
		// Act
		AchTransferOrderAggregateRoot signedTransferOrder = sut.signAsFirst(signerRuleId, signDateTime,
				refinedSecondSignerCandidateIds);

		// Assert
		assertTrue(signedTransferOrder.isPendingSecondSignature());
		assertTrue(signedTransferOrder.getFirstSignatureDateTime().isPresent());
		assertTrue(signedTransferOrder.getFirstSignerRuleId().isPresent());
		assertEquals(signDateTime, signedTransferOrder.getFirstSignatureDateTime().get());
		assertEquals(signerRuleId, signedTransferOrder.getFirstSignerRuleId().get());
		assertTrue(signedTransferOrder.getFirstSignerCandidateIds().isEmpty());
		assertFalse(signedTransferOrder.getSecondSignerCandidateIds().isEmpty());
		assertEquals(refinedSecondSignerCandidateIds, signedTransferOrder.getSecondSignerCandidateIds());
	}

	@Test
	void sign_as_first_an_already_first_signed_ach_transfer_order_should_throw_IllegalStateException() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId firstSignerRuleId = new FinanceOfficerRuleId(111305254L);
		Long signDateTime = System.currentTimeMillis();
		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = Arrays
				.asList(new FinanceOfficerRuleId(1113091628L), new FinanceOfficerRuleId(111254840L));
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);

		// Act & Assert
		assertThrows(IllegalStateException.class,
				() -> sut.signAsFirst(firstSignerRuleId, signDateTime, refinedSecondSignerCandidateIds));
	}

	@Test
	void sign_as_first_ach_transfer_order_signer_is_not_candidate_should_throw_FinanceOfficerRuleIsNotSignCandidateException() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId invalidSignerId = new FinanceOfficerRuleId(111254840L);
		Long signDateTime = System.currentTimeMillis();
		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = Arrays
				.asList(new FinanceOfficerRuleId(1113091628L), new FinanceOfficerRuleId(111254840L));
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(orderId);

		// Act & Assert
		assertThrows(FinanceOfficerRuleIsNotSignCandidateException.class, () -> {
			sut.signAsFirst(invalidSignerId, signDateTime, refinedSecondSignerCandidateIds);
		});
	}

	@Test
	void sign_ach_transfer_order_as_second_should_be_successful() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId secondSignerRuleId = new FinanceOfficerRuleId(1112504840L);
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);
		Long signDateTime = System.currentTimeMillis();

		// Act
		AchTransferOrderAggregateRoot signedAchTransferOrder = sut.signAsSecond(secondSignerRuleId, signDateTime);

		// Assert
		assertTrue(signedAchTransferOrder.isPendingSend());
		assertTrue(signedAchTransferOrder.getSecondSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getSecondSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getSecondSignatureDateTime().get(), signDateTime);
		assertEquals(signedAchTransferOrder.getSecondSignerRuleId().get(), secondSignerRuleId);
		assertTrue(signedAchTransferOrder.getFirstSignerCandidateIds().isEmpty());
		assertTrue(signedAchTransferOrder.getSecondSignerCandidateIds().isEmpty());
	}

	@Test
	void sign_as_second_ach_transfer_order_with_pending_first_signature_state_should_throw_IllegalStateException() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId secondSignerRuleId = new FinanceOfficerRuleId(1112504840L);
		Long signDateTime = System.currentTimeMillis();
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(orderId);

		// Act & Assert
		assertThrows(IllegalStateException.class, () -> sut.signAsSecond(secondSignerRuleId, signDateTime));
	}

	@Test
	void sign_as_second_ach_transfer_order_by_the_same_signer_as_signer1_should_throw_AchTransferOrderSigner1AndSigner2CannotBeTheSameException() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId invalidSignerRuleId = new FinanceOfficerRuleId(1113005254L);
		Long signDateTime = System.currentTimeMillis();
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);

		// Act & Assert
		assertThrows(AchTransferOrderSigner1AndSigner2CannotBeTheSameException.class, () -> {
			sut.signAsSecond(invalidSignerRuleId, signDateTime);
		});
	}
	
	@Test
	void sign_as_second_ach_transfer_order_when_signer_is_not_a_candidate_should_throw_FinanceOfficerRuleIsNotSignCandidateException() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId invalidSignerRuleId = new FinanceOfficerRuleId(1113226440L);
		Long signDateTime = System.currentTimeMillis();
		AchTransferOrderAggregateRoot sut = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);

		// Act & Assert
		assertThrows(FinanceOfficerRuleIsNotSignCandidateException.class, () -> {
			sut.signAsSecond(invalidSignerRuleId, signDateTime);
		});
	}
}
