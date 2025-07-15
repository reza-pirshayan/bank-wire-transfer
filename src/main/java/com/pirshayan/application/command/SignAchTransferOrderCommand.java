package com.pirshayan.application.command;

import java.util.Optional;

public class SignAchTransferOrderCommand {
	private final Long signerRuleId;
	private final String achTransferOrderId;

	public SignAchTransferOrderCommand(Long signerRuleId, String achTransferOrderId) {
		this.signerRuleId = signerRuleId;
		this.achTransferOrderId = achTransferOrderId;
	}

	public Long getSignerRuleId() {
		return Optional.ofNullable(signerRuleId)
				.orElseThrow(() -> new IllegalArgumentException("Signer ID cannot be EMPTY."));
	}

	public String getAchTransferOrderId() {
		return Optional.ofNullable(achTransferOrderId)
				.orElseThrow(() -> new IllegalArgumentException("ACH transfer order ID cannot be EMPTY."));
	}
}
