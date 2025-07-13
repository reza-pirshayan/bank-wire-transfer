package com.pirshayan.infrastructure.persistence.financeofficerrule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "financeOfficerRules")
public class FinanceOfficerRuleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "allowed_to_sign_as_first", nullable = false)
	private Boolean allowedToSignAsFirst;

	@Column(name = "allowed_to_sign_as_second", nullable = false)
	private Boolean allowedToSignAsSecond;

	@Column(name = "allowed_to_send", nullable = false)
	private Boolean allowedToSend;

	@Column(name = "max_first_sign_amount", nullable = false)
	private Long maxFirstSignAmount;

	@Column(name = "max_second_sign_amount", nullable = false)
	private Long maxSecondSignAmount;

	@Column(name = "max_send_amount", nullable = false)
	private Long maxSendAmount;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "position_id", referencedColumnName = "id")
	private PositionEntity positionEntity;

	@Version
	private Integer version;

	public FinanceOfficerRuleEntity() {
	}

	public FinanceOfficerRuleEntity(Long id, Boolean allowedToSignAsFirst, Boolean allowedToSignAsSecond,
			Boolean allowedToSend, Long maxFirstSignAmount, Long maxSecondSignAmount, Long maxSendAmount,
			PositionEntity positionEntity) {
		this.id = id;
		this.allowedToSignAsFirst = allowedToSignAsFirst;
		this.allowedToSignAsSecond = allowedToSignAsSecond;
		this.allowedToSend = allowedToSend;
		this.maxFirstSignAmount = maxFirstSignAmount;
		this.maxSecondSignAmount = maxSecondSignAmount;
		this.maxSendAmount = maxSendAmount;
		this.positionEntity = positionEntity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getAllowedToSignAsFirst() {
		return allowedToSignAsFirst;
	}

	public void setAllowedToSignAsFirst(Boolean allowedToSignAsFirst) {
		this.allowedToSignAsFirst = allowedToSignAsFirst;
	}

	public Boolean getAllowedToSignAsSecond() {
		return allowedToSignAsSecond;
	}

	public void setAllowedToSignAsSecond(Boolean allowedToSignAsSecond) {
		this.allowedToSignAsSecond = allowedToSignAsSecond;
	}

	public Boolean getAllowedToSend() {
		return allowedToSend;
	}

	public void setAllowedToSend(Boolean allowedToSend) {
		this.allowedToSend = allowedToSend;
	}

	public Long getMaxFirstSignAmount() {
		return maxFirstSignAmount;
	}

	public void setMaxFirstSignAmount(Long maxFirstSignAmount) {
		this.maxFirstSignAmount = maxFirstSignAmount;
	}

	public Long getMaxSecondSignAmount() {
		return maxSecondSignAmount;
	}

	public void setMaxSecondSignAmount(Long maxSecondSignAmount) {
		this.maxSecondSignAmount = maxSecondSignAmount;
	}

	public Long getMaxSendAmount() {
		return maxSendAmount;
	}

	public void setMaxSendAmount(Long maxSendAmount) {
		this.maxSendAmount = maxSendAmount;
	}

	public PositionEntity getPositionEntity() {
		return positionEntity;
	}

	public void setPositionEntity(PositionEntity positionEntity) {
		this.positionEntity = positionEntity;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
