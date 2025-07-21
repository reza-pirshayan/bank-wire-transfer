package com.pirshayan.domain.repository;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;

public interface AchTransferOrderAggregateRepository {
	public AchTransferOrderAggregateRoot findById(AchTransferOrderId achTransferOrderId);

	public void create(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot);

	public void update(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot);

	public void clearPersistenceContext();

	public void deleteById(AchTransferOrderId achTransferOrderId);
}
