package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AchTransferOrderAbstract {
	@Id
	@Column(name = "order_id", length = 15)
	private String orderId;

	public AchTransferOrderAbstract() {
	}

	public AchTransferOrderAbstract(String orderId) {
		super();
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
