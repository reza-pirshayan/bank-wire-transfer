package com.pirshayan.application.command;

import java.util.Optional;

public class CancelAchTransferOrderCommand {
	private final String achTransferOrderId;

	public CancelAchTransferOrderCommand(String achTransferOrderId) {
		this.achTransferOrderId = achTransferOrderId;
	}

	public String getAchTransferOrderId() {
		return Optional.ofNullable(achTransferOrderId)
				.orElseThrow(() -> new IllegalArgumentException("ACH transfer order ID cannot be EMPTY."));
	}

}
