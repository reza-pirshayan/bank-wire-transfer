package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AchTransferOrderMapperHandler {
	private final PendingFirstSignatureAchTransferOrderMapperImpl pendingFirstSignatureAchTransferOrderMapperImpl;
	private final PendingSecondSignatureAchTransferOrderMapperImpl pendingSecondSignatureAchTransferOrderMapperImpl;
	private final PendingSendAchTransferOrderMapperImpl pendingSendAchTransferOrderMapperImpl;

	public AchTransferOrderMapperHandler(
			PendingFirstSignatureAchTransferOrderMapperImpl pendingFirstSignatureAchTransferOrderMapperImpl,
			PendingSecondSignatureAchTransferOrderMapperImpl pendingSecondSignatureAchTransferOrderMapperImpl,
			PendingSendAchTransferOrderMapperImpl pendingSendAchTransferOrderMapperImpl) {
		this.pendingFirstSignatureAchTransferOrderMapperImpl = pendingFirstSignatureAchTransferOrderMapperImpl;
		this.pendingSecondSignatureAchTransferOrderMapperImpl = pendingSecondSignatureAchTransferOrderMapperImpl;
		this.pendingSendAchTransferOrderMapperImpl = pendingSendAchTransferOrderMapperImpl;
	}

	public AchTransferOrderMapper getMapper(AchTransferOrderEntity achTransferOrderEntity) {
		if (achTransferOrderEntity == null) {
			throw new IllegalArgumentException("achTransferOrderEntity must not be null");
		}
		
		switch (achTransferOrderEntity.getStatus()) {
		case PENDING_FIRST_SIGNATURE -> {
			return pendingFirstSignatureAchTransferOrderMapperImpl;
		}
		case PENDING_SECOND_SIGNATURE -> {
			return pendingSecondSignatureAchTransferOrderMapperImpl;
		}
		case PENDING_SEND -> {
			return pendingSendAchTransferOrderMapperImpl;
		}
		default -> {
			throw new IllegalStateException(
					String.format("ACH transfer order entity with ID [ %s ] and status [ %s ] cannot be mapped",
							achTransferOrderEntity.getOrderId(), achTransferOrderEntity.getStatus()));
		}
		}
	}

	public AchTransferOrderMapper getMapper(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {
		if (achTransferOrderAggregateRoot == null) {
			throw new IllegalArgumentException("achTransferOrderAggregateRoot must not be null");
		}
		
		if (achTransferOrderAggregateRoot.isPendingFirstSignature()) {
			return pendingFirstSignatureAchTransferOrderMapperImpl;
		}
		
		if (achTransferOrderAggregateRoot.isPendingSecondSignature()) {
			return pendingSecondSignatureAchTransferOrderMapperImpl;
		}
		
		if (achTransferOrderAggregateRoot.isPendingSend()) {
			return pendingSendAchTransferOrderMapperImpl;
		}
		
		throw new IllegalStateException(
				String.format("ACH transfer order aggregate with ID [ %s ] and status [ %s ] cannot be mapped",
						achTransferOrderAggregateRoot.getAchTransferOrderId().getId(),
						achTransferOrderAggregateRoot.getStatusString()));

	}
}
