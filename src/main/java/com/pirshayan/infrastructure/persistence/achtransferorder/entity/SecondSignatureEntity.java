package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "secondSignatures")
public class SecondSignatureEntity extends AchTransferOrderAbstract {
	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	FirstSignatureEntity firstSignatureEntity;

	@Embedded
	SignatureInfo signatureInfo;

	public SecondSignatureEntity() {
	}

	public SecondSignatureEntity(String id, FirstSignatureEntity firstSignatureEntity, SignatureInfo signatureInfo) {
		super(id);
		this.firstSignatureEntity = firstSignatureEntity;
		this.signatureInfo = signatureInfo;
	}

	public FirstSignatureEntity getFirstSignatureEntity() {
		return firstSignatureEntity;
	}

	public void setFirstSignatureEntity(FirstSignatureEntity firstSignatureEntity) {
		this.firstSignatureEntity = firstSignatureEntity;
	}

	public SignatureInfo getSignatureInfo() {
		return signatureInfo;
	}

	public void setSignatureInfo(SignatureInfo signatureInfo) {
		this.signatureInfo = signatureInfo;
	}

}
