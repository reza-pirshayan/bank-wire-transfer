package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "secondSignerCandidates")
@NamedQuery(name = "SecondSignerCandidateEntity.findByAchTransferOrderEntityId.list", query = "select s from SecondSignerCandidateEntity as s where s.achTransferOrderEntity.orderId = :order_id")
@NamedQuery(name = "SecondSignerCandidateEntity.deleteByAchTransferOrderEntityId:void", query = "delete from SecondSignerCandidateEntity as s where s.achTransferOrderEntity.orderId = :order_id")
public class SecondSignerCandidateEntity extends SignerCandidate {

	public SecondSignerCandidateEntity() {
		super();
	}
	
	public SecondSignerCandidateEntity(Long id, AchTransferOrderEntity achTransferOrderEntity, Long signerId) {
		super(id, achTransferOrderEntity, signerId);
	}
	
	public SecondSignerCandidateEntity(AchTransferOrderEntity achTransferOrderEntity, Long signerId) {
		super(achTransferOrderEntity, signerId);
	}
}
