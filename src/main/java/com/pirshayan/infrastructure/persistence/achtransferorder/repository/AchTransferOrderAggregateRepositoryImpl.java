package com.pirshayan.infrastructure.persistence.achtransferorder.repository;

import com.pirshayan.domain.model.achtransferorder.AchTransferOrderAggregateRoot;
import com.pirshayan.domain.model.achtransferorder.AchTransferOrderId;
import com.pirshayan.domain.repository.AchTransferOrderAggregateRepository;
import com.pirshayan.infrastructure.persistence.achtransferorder.entity.AchTransferOrderEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AchTransferOrderAggregateRepositoryImpl implements AchTransferOrderAggregateRepository {
	AchTransferOrderEntityRepository achTransferOrderEntityRepository;

	public AchTransferOrderAggregateRepositoryImpl(AchTransferOrderEntityRepository achTransferOrderEntityRepository) {
		this.achTransferOrderEntityRepository = achTransferOrderEntityRepository;
	}

	@Override
	public AchTransferOrderAggregateRoot findById(AchTransferOrderId achTransferOrderId) {
		AchTransferOrderEntity achTransferOrderEntity = achTransferOrderEntityRepository.findById(achTransferOrderId.getId());
		return null;
	}

	@Override
	public void update(AchTransferOrderAggregateRoot achTransferOrderAggregateRoot) {
		// TODO Auto-generated method stub
		
	}

}
