package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "firstSignerCandidates")
@NamedQuery(name = "FirstSignerCandidateEntity.findByAchTransferOrderEntityId:list", query = "select f from FirstSignerCandidateEntity as f where f.achTransferOrderEntity.orderId = :order_id")
@NamedQuery(name = "FirstSignerCandidateEntity.deleteByAchTransferOrderEntityId:void", query = "delete from FirstSignerCandidateEntity as f where f.achTransferOrderEntity.orderId = :order_id")
@NamedQuery(name = "FirstSignerCandidateEntity.deleteByAchTransferOrderEntityIds:void", query = "delete from FirstSignerCandidateEntity as f where f.achTransferOrderEntity.orderId = :achTransferOrderEntityIds")
public class FirstSignerCandidateEntity extends SignerCandidate {

	public FirstSignerCandidateEntity() {
	}
	
	public FirstSignerCandidateEntity(Long id, AchTransferOrderEntity achTransferOrderEntity, Long signerId) {
		super(id, achTransferOrderEntity, signerId);
	}
	
	public FirstSignerCandidateEntity(AchTransferOrderEntity achTransferOrderEntity, Long signerId) {
		super(achTransferOrderEntity, signerId);
	}
}
