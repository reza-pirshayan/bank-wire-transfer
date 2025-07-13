package com.pirshayan;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.model.financeofficerrule.FinanceOfficerRuleId;

public class AchTransferOrderAggregateTestHelper {
	private static AchTransferOrderAggregateRoot.Builder getAchTransferOrderAggregateRootBuilder() {
		AchTransferOrderId achTransferOrderId = new AchTransferOrderId("2025071200001");
		Long receivedDateTime = System.currentTimeMillis();
		String transferId = "20250712/50/01";
		Long transferAmount = 2000000L;
		LocalDate transferDateOfIssue = LocalDate.now();
		String transferDestinationBankAccountIban = "IR830550010180000148842002";
		String transferDestinationBankAccountOwnerId = "0062373374";
		String transferDestinationBankAccountOwnerName = "A.Reza Pirshayan";
		String transferOwnerId = "0062373374";
		String transferOwnerName = "A.Reza Pirshayan";
		Integer transferChecksum = 0;

		return new AchTransferOrderAggregateRoot.Builder(achTransferOrderId, receivedDateTime, transferId,
				transferAmount, transferDateOfIssue, transferDestinationBankAccountIban,
				transferDestinationBankAccountOwnerId, transferDestinationBankAccountOwnerName, transferOwnerId,
				transferOwnerName, transferChecksum);
	}

	public static AchTransferOrderAggregateRoot buildPendingFirstSignatureAchTransferOrder() {
		return getAchTransferOrderAggregateRootBuilder()
				.setFirstSignerCandidateIds(
						Arrays.asList(new FinanceOfficerRuleId(1113018645L), new FinanceOfficerRuleId(1113222145L),
								new FinanceOfficerRuleId(1113226189L), new FinanceOfficerRuleId(1113226440L),
								new FinanceOfficerRuleId(111305254L), new FinanceOfficerRuleId(1113091628L)))
				.setSecondSignerCandidateIds(Arrays.asList(new FinanceOfficerRuleId(1113018645L),
						new FinanceOfficerRuleId(1113222145L), new FinanceOfficerRuleId(1113226189L),
						new FinanceOfficerRuleId(1113226440L), new FinanceOfficerRuleId(111305254L),
						new FinanceOfficerRuleId(1113091628L), new FinanceOfficerRuleId(111254840L)))
				.build();
	}
	
	public static AchTransferOrderAggregateRoot buildPendingSecondSignatureAchTransferOrder() {
		FinanceOfficerRuleId signerRuleId = new FinanceOfficerRuleId(111305254L);
		Long signDateTime = System.currentTimeMillis();
		List<FinanceOfficerRuleId> refinedSecondSignerCandidateIds = Arrays
				.asList(new FinanceOfficerRuleId(1113091628L), new FinanceOfficerRuleId(111254840L));
		AchTransferOrderAggregateRoot pendingFirstSignatureAchTransferOrder = buildPendingFirstSignatureAchTransferOrder();
		return pendingFirstSignatureAchTransferOrder.signAsFirst(signerRuleId, signDateTime, refinedSecondSignerCandidateIds);
	}
}
