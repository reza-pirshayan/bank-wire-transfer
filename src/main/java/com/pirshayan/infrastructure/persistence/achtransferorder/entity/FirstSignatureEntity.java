package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "firstSignatures")
public class FirstSignatureEntity extends AchTransferOrderAbstract {
	@MapsId
	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	private AchTransferOrderEntity achTransferOrderEntity;

	@Embedded
	private SignatureInfo signatureInfo;

	public FirstSignatureEntity() {
	}

	public FirstSignatureEntity(String id, AchTransferOrderEntity achTransferOrderEntity, SignatureInfo signatureInfo) {
		super(id);
		this.achTransferOrderEntity = achTransferOrderEntity;
		this.signatureInfo = signatureInfo;
	}

	public AchTransferOrderEntity getAchTransferOrderEntity() {
		return achTransferOrderEntity;
	}

	public void setAchTransferOrderEntity(AchTransferOrderEntity achTransferOrderEntity) {
		this.achTransferOrderEntity = achTransferOrderEntity;
	}

	public SignatureInfo getSignatureInfo() {
		return signatureInfo;
	}

	public void setSignatureInfo(SignatureInfo signatureInfo) {
		this.signatureInfo = signatureInfo;
	}

}
