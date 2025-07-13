package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SignatureInfo {
	@Column(name = "date_time", columnDefinition = "NUMBER", nullable = false)
	private Long dateTime;

	@Column(name = "signer_id", columnDefinition = "NUMBER", nullable = false)
	private Long signerId;

	public SignatureInfo() {
	}

	public SignatureInfo(Long dateTime, Long signerId) {
		this.dateTime = dateTime;
		this.signerId = signerId;
	}

	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	public Long getSignerId() {
		return signerId;
	}

	public void setSignerId(Long signerId) {
		this.signerId = signerId;
	}

}
