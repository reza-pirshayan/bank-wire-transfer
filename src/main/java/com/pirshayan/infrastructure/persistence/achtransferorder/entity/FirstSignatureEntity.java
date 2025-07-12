package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "firstSignature")
public class FirstSignatureEntity extends AchTransferOrderAbstract {

	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	private AchTransferOrderEntity achTransfeEntity;

	@Embedded
	private SignatureInfo signatureInfo;

	public FirstSignatureEntity() {
	}

	public FirstSignatureEntity(String id, AchTransferOrderEntity achTransfeEntity, SignatureInfo signatureInfo) {
		super(id);
		this.achTransfeEntity = achTransfeEntity;
		this.signatureInfo = signatureInfo;
	}

	public AchTransferOrderEntity getAchTransfeEntity() {
		return achTransfeEntity;
	}

	public void setAchTransfeEntity(AchTransferOrderEntity achTransfeEntity) {
		this.achTransfeEntity = achTransfeEntity;
	}

	public SignatureInfo getSignatureInfo() {
		return signatureInfo;
	}

	public void setSignatureInfo(SignatureInfo signatureInfo) {
		this.signatureInfo = signatureInfo;
	}

}
