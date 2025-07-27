package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AchTransferOrderAbstract {
	@Id
	@Column(name = "order_id", length = 15)
	private String orderId;

	protected AchTransferOrderAbstract() {
	}

	protected AchTransferOrderAbstract(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
