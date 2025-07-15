package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SignerCandidate {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
	private AchTransferOrderEntity achTransferOrderEntity;

	@Column(name = "signer_id", nullable = false)
	private Long signerId;

	public SignerCandidate() {
	}

	public SignerCandidate(Long id, AchTransferOrderEntity achTransferOrderEntity, Long signerId) {
		this.id = id;
		this.achTransferOrderEntity = achTransferOrderEntity;
		this.signerId = signerId;
	}
	
	public SignerCandidate(AchTransferOrderEntity achTransferOrderEntity, Long signerId) {
		this.achTransferOrderEntity = achTransferOrderEntity;
		this.signerId = signerId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AchTransferOrderEntity getAchTransferOrderEntity() {
		return achTransferOrderEntity;
	}

	public void setAchTransferOrderEntity(AchTransferOrderEntity achTransferOrderEntity) {
		this.achTransferOrderEntity = achTransferOrderEntity;
	}

	public Long getSignerId() {
		return signerId;
	}

	public void setSignerId(Long signerId) {
		this.signerId = signerId;
	}

}
