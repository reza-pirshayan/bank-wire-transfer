package com.pirshayan.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.pirshayan.AchTransferOrderAggregateTestHelper;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.exception.AchTransferOrderSigner1AndSigner2CannotBeTheSameException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.financeofficerrule.exception.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.repository.FinanceOfficerRuleAggregateRepository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class AchTransferOrderDomainServiceTest {
	@Inject
	private FinanceOfficerRuleAggregateRepository financeOfficerRuleAggregateRepository;

	@Inject
	private AchTransferOrderDomainService sut;

	@Test
	void sign_pending_ach_transfer_order_with_status_pending_first_signature__should_be_successful() {
		// Arrange
		String orderId = "025071200001";
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(1113005254L);
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(signerRuleId);
		AchTransferOrderAggregateRoot pendingFirstAchTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(orderId);
		List<FinanceOfficerRuleId> refinedSecondSignerCandidatesIds = Arrays.asList(
				new FinanceOfficerRuleId(1113018645L), new FinanceOfficerRuleId(1113091628L),
				new FinanceOfficerRuleId(1112504840L));

		// Act
		AchTransferOrderAggregateRoot signedAchTransferOrder = sut.sign(signerRule, pendingFirstAchTransferOrder);

		// Arrange
		assertTrue(signedAchTransferOrder.isPendingSecondSignature());
		assertTrue(signedAchTransferOrder.getFirstSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getFirstSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getFirstSignerRuleId().get(), signerRuleId);
		assertTrue(signedAchTransferOrder.getFirstSignerCandidateRuleIds().isEmpty());
		assertEquals(signedAchTransferOrder.getSecondSignerCandidateRuleIds(), refinedSecondSignerCandidatesIds);
	}

	@Test
	void sign_ach_transfer_order_by_a_signer_without_first_sign_permission__should_throw_FinanceOfficerNotPrivilegedToSignAsFirstSignerException() {
		// Arrange
		String orderId = "025071200001";
		AchTransferOrderAggregateRoot pendingFirstAchTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingFirstSignatureAchTransferOrder(orderId);
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(1113222145L);
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(signerRuleId);

		// Act & Assert
		assertThrows(FinanceOfficerNotPrivilegedToSignAsFirstSignerException.class,
				() -> sut.sign(signerRule, pendingFirstAchTransferOrder));
	}

	@Test
	void sign_ach_transfer_order_with_status_pending_second_signature__should_be_successful() {
		// Arrange
		String orderId = "025071200001";
		AchTransferOrderAggregateRoot pendingSecondAchTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(1112504840L);
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(signerRuleId);

		// Act
		AchTransferOrderAggregateRoot signedAchTransferOrder = sut.sign(signerRule, pendingSecondAchTransferOrder);

		// Assert
		assertTrue(signedAchTransferOrder.isPendingBankDispatch());
		assertTrue(signedAchTransferOrder.getSecondSignerRuleId().isPresent());
		assertTrue(signedAchTransferOrder.getSecondSignatureDateTime().isPresent());
		assertEquals(signedAchTransferOrder.getSecondSignerRuleId().get(), signerRule.getFinanceOfficerRuleId());
		assertTrue(signedAchTransferOrder.getSecondSignerCandidateRuleIds().isEmpty());
	}

	@Test
	void sign_ach_transfer_order_with_status_pending_second_signature_by_a_signer_without_second_sign_permission__should_throw_FinanceOfficerNotPrivilegedToSignAsSecondSignerException() {
		// Arrange
		String orderId = "025071200001";
		AchTransferOrderAggregateRoot pendingSecondAchTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);
		FinanceOfficerRuleId signerId = new FinanceOfficerRuleId(1113018645L);
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(signerId);

		// Act & Assert
		assertThrows(FinanceOfficerNotPrivilegedToSignAsSecondSignerException.class,
				() -> sut.sign(signerRule, pendingSecondAchTransferOrder));
	}

	@Test
	void sign_ach_transfer_order_with_status_pending_second_signature_by_first_signer__should_throw_AchTransferOrderSigner1AndSigner2CannotBeTheSameException() {
		// Arrange
		String orderId = "025071200001";
		AchTransferOrderAggregateRoot pendingSecondAchTransferOrder = AchTransferOrderAggregateTestHelper
				.buildPendingSecondSignatureAchTransferOrder(orderId);
		FinanceOfficerRuleId financeOfficerId = new FinanceOfficerRuleId(1113005254L);
		FinanceOfficerRuleAggregateRoot signerRule = financeOfficerRuleAggregateRepository.findById(financeOfficerId);

		// Act & Assert
		assertThrows(AchTransferOrderSigner1AndSigner2CannotBeTheSameException.class,
				() -> sut.sign(signerRule, pendingSecondAchTransferOrder));
	}

}
