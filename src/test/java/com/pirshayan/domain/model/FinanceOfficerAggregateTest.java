package com.pirshayan.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.pirshayan.domain.model.exception.financeofficer.FinanceOfficerNotPrivilegedToSignAsFirstSignerException;
import com.pirshayan.domain.model.exception.financeofficer.FinanceOfficerNotPrivilegedToSignAsSecondSignerException;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleAggregateRoot;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

class FinanceOfficerAggregateTest {
	@Test
	void ensure_sufficient_privileges_for_first_signature_should_be_successful() {
		// Arrange
		FinanceOfficerRuleId financeOfficerRuleId = new FinanceOfficerRuleId(1113002529L);
		Boolean isAllowedToSignAsFirst = true;
		Boolean isAllowedToSignAsSecond = true;
		Boolean isAllowedToSend = true;
		Long maxFirstSignAmount = 5000000L;
		Long maxSecondSignAmount = 5000000L;
		Long maxSendAmount = 5000000L;
		FinanceOfficerRuleAggregateRoot sut = new FinanceOfficerRuleAggregateRoot.Builder(financeOfficerRuleId,
				isAllowedToSignAsFirst, isAllowedToSignAsSecond, isAllowedToSend, maxFirstSignAmount,
				maxSecondSignAmount, maxSendAmount).build();

		// Act and Assert
		assertDoesNotThrow(() -> sut.ensureSufficientPrivilegesForFirstSignature(3000000L));

	}

	@Test
	void ensure_sufficient_privileges_for_first_signature_when_is_not_allowed_to_perform_second_signature_should_throw_FinanceOfficerNotPrivilegedToSignAsFirstSignerException() {
		// Arrange
		FinanceOfficerRuleId financeOfficerRuleId = new FinanceOfficerRuleId(1113002529L);
		Boolean isAllowedToSignAsFirst = false;
		Boolean isAllowedToSignAsSecond = true;
		Boolean isAllowedToSend = true;
		Long maxFirstSignAmount = 5000000L;
		Long maxSecondSignAmount = 5000000L;
		Long maxSendAmount = 5000000L;
		FinanceOfficerRuleAggregateRoot sut = new FinanceOfficerRuleAggregateRoot.Builder(financeOfficerRuleId,
				isAllowedToSignAsFirst, isAllowedToSignAsSecond, isAllowedToSend, maxFirstSignAmount,
				maxSecondSignAmount, maxSendAmount).build();

		// Act & Assert
		assertThrows(FinanceOfficerNotPrivilegedToSignAsFirstSignerException.class,
				() -> sut.ensureSufficientPrivilegesForFirstSignature(3000000L));
	}

	@Test
	void ensure_sufficient_privileges_for_first_signature_when_amount_is_greater_than_max_allowed_amount_should_throw_FinanceOfficerNotPrivilegedToSignAsFirstSignerException() {
		// Arrange
		FinanceOfficerRuleId financeOfficerRuleId = new FinanceOfficerRuleId(1113002529L);
		Boolean isAllowedToSignAsFirst = true;
		Boolean isAllowedToSignAsSecond = true;
		Boolean isAllowedToSend = true;
		Long maxFirstSignAmount = 5000000L;
		Long maxSecondSignAmount = 5000000L;
		Long maxSendAmount = 5000000L;
		FinanceOfficerRuleAggregateRoot sut = new FinanceOfficerRuleAggregateRoot.Builder(financeOfficerRuleId,
				isAllowedToSignAsFirst, isAllowedToSignAsSecond, isAllowedToSend, maxFirstSignAmount,
				maxSecondSignAmount, maxSendAmount).build();

		// Act & Assert
		assertThrows(FinanceOfficerNotPrivilegedToSignAsFirstSignerException.class,
				() -> sut.ensureSufficientPrivilegesForFirstSignature(6000000L));
	}

	@Test
	void ensure_sufficient_privileges_second_signature_should_be_successful() {
		// Arrange
		FinanceOfficerRuleId financeOfficerRuleId = new FinanceOfficerRuleId(1113002529L);
		Boolean isAllowedToSignAsFirst = true;
		Boolean isAllowedToSignAsSecond = true;
		Boolean isAllowedToSend = true;
		Long maxFirstSignAmount = 5000000L;
		Long maxSecondSignAmount = 5000000L;
		Long maxSendAmount = 5000000L;
		FinanceOfficerRuleAggregateRoot sut = new FinanceOfficerRuleAggregateRoot.Builder(financeOfficerRuleId,
				isAllowedToSignAsFirst, isAllowedToSignAsSecond, isAllowedToSend, maxFirstSignAmount,
				maxSecondSignAmount, maxSendAmount).build();

		// Act
		assertDoesNotThrow(() -> sut.ensureSufficientPrivilegesForSecondSignature(3000000L));

	}

	@Test
	void ensure_sufficient_privileges_for_second_signature_when_is_not_allowed_to_perform_second_signature_should_throw_FinanceOfficerNotPrivilegedToSignAsFirstSignerException() {
		// Arrange
		FinanceOfficerRuleId financeOfficerRuleId = new FinanceOfficerRuleId(1113002529L);
		Boolean isAllowedToSignAsFirst = true;
		Boolean isAllowedToSignAsSecond = false;
		Boolean isAllowedToSend = true;
		Long maxFirstSignAmount = 5000000L;
		Long maxSecondSignAmount = 5000000L;
		Long maxSendAmount = 5000000L;
		FinanceOfficerRuleAggregateRoot sut = new FinanceOfficerRuleAggregateRoot.Builder(financeOfficerRuleId,
				isAllowedToSignAsFirst, isAllowedToSignAsSecond, isAllowedToSend, maxFirstSignAmount,
				maxSecondSignAmount, maxSendAmount).build();

		// Act & Assert
		assertThrows(FinanceOfficerNotPrivilegedToSignAsSecondSignerException.class,
				() -> sut.ensureSufficientPrivilegesForSecondSignature(3000000L));
	}

	@Test
	void ensure_sufficient_privileges_for_second_signature_when_amount_is_greater_than_max_allowed_amount_should_throw_FinanceOfficerNotPrivilegedToSignAsFirstSignerException() {
		// Arrange
		FinanceOfficerRuleId financeOfficerRuleId = new FinanceOfficerRuleId(1113002529L);
		Boolean isAllowedToSignAsFirst = true;
		Boolean isAllowedToSignAsSecond = true;
		Boolean isAllowedToSend = true;
		Long maxFirstSignAmount = 5000000L;
		Long maxSecondSignAmount = 5000000L;
		Long maxSendAmount = 5000000L;
		FinanceOfficerRuleAggregateRoot sut = new FinanceOfficerRuleAggregateRoot.Builder(financeOfficerRuleId,
				isAllowedToSignAsFirst, isAllowedToSignAsSecond, isAllowedToSend, maxFirstSignAmount,
				maxSecondSignAmount, maxSendAmount).build();

		// Act & Assert
		assertThrows(FinanceOfficerNotPrivilegedToSignAsSecondSignerException.class,
				() -> sut.ensureSufficientPrivilegesForSecondSignature(6000000L));
	}
}
