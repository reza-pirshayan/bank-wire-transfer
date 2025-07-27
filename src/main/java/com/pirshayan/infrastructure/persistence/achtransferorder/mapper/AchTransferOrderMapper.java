package com.pirshayan.infrastructure.persistence.achtransferorder.mapper;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderAbstract;

public interface AchTransferOrderMapper {
	public AchTransferOrderAggregateRoot toModel(AchTransferOrderAbstract  entity);

	public AchTransferOrderAbstract toEntity(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot);
}
