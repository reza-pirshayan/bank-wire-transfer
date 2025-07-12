package com.pirshayan.application.command;

import java.util.Optional;

public class SignAchTransferOrderCommand {
	private final Long signerId;
	private final String achTransferOrderId;

	public SignAchTransferOrderCommand(Long signerId, String achTransferOrderId) {
		this.signerId = signerId;
		this.achTransferOrderId = achTransferOrderId;
	}

	public Long getSignerId() {
		return Optional.ofNullable(signerId)
				.orElseThrow(() -> new IllegalArgumentException("Signer ID cannot be EMPTY."));
	}

	public String getAchTransferOrderId() {
		return Optional.ofNullable(achTransferOrderId)
				.orElseThrow(() -> new IllegalArgumentException("ACH transfer order ID cannot be EMPTY."));
	}
}
