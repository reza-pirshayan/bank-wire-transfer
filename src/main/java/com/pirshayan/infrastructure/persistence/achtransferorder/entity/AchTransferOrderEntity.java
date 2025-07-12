package com.pirshayan.infrastructure.persistence.achtransferorder.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@Entity
@Table(name = "achTransferOrders")
public class AchTransferOrderEntity extends AchTransferOrderAbstract {
	@Column(name = "received_date_time", columnDefinition = "NUMBER")
	private Long receivedDateTime;

	@Column(name = "transfer_no", length = 20)
	private String transferNo;

	@Column(name = "amount", columnDefinition = "NUMBER")
	private Long amount;

	@Column(name = "date_of_issue", columnDefinition = "NUMBER")
	private Long dateOfIssue;

	@Column(name = "destination_iban", columnDefinition = "CHAR(26)")
	private String destinationIban;

	@Column(name = "account_owner_national_code", length = 30)
	private String accountOwnerNationalCode;

	@Column(name = "account_owner_name", length = 50)
	private String accountOwnerName;

	@Column(name = "account_owner_person_type", columnDefinition = "CHAR(1)")
	private String accountOwnerPersonType;

	@Column(name = "account_owner_mobile_no")
	private String accountOwnerMobileNumber;

	@Column(name = "transfer_owner_national_code", length = 30)
	private String transferOwnerNationalCode;

	@Column(name = "transfer_ownr_name", length = 50)
	private String transferOwnerName;

	@Column(name = "checksum", columnDefinition = "NUMBER")
	private Integer checksum;

	@Column(name = "transfer_description", length = 50)
	private String transerDescriprion;

	@Column(name = "transfer_pay_id", length = 30)
	private String transferPayId;

	@Column(name = "status", columnDefinition = "NUMBER")
	private Integer status;

	@Transient
	private List<FirstSignerCandidateEntity> firstSignerCandidateEntities;

	@Transient
	private List<SecondSignerCandidateEntity> secondSignerCandidateEntities;

	@Version
	private Integer version;

	public AchTransferOrderEntity() {
	}

	public AchTransferOrderEntity(String id, Long receivedDateTime, String transferNo, Long amount, Long dateOfIssue,
			String destinationIban, String accountOwnerNationalCode, String accountOwnerName,
			String accountOwnerPersonType, String accountOwnerMobileNumber, String transferOwnerNationalCode,
			String transferOwnerName, Integer checksum, String transerDescriprion, String transferPayId, Integer status) {
		super(id);
		this.receivedDateTime = receivedDateTime;
		this.transferNo = transferNo;
		this.amount = amount;
		this.dateOfIssue = dateOfIssue;
		this.destinationIban = destinationIban;
		this.accountOwnerNationalCode = accountOwnerNationalCode;
		this.accountOwnerName = accountOwnerName;
		this.accountOwnerPersonType = accountOwnerPersonType;
		this.accountOwnerMobileNumber = accountOwnerMobileNumber;
		this.transferOwnerNationalCode = transferOwnerNationalCode;
		this.transferOwnerName = transferOwnerName;
		this.checksum = checksum;
		this.transerDescriprion = transerDescriprion;
		this.transferPayId = transferPayId;
		this.status = status;
		this.version = version;
	}

	public Long getReceivedDateTime() {
		return receivedDateTime;
	}

	public void setReceivedDateTime(Long receivedDateTime) {
		this.receivedDateTime = receivedDateTime;
	}

	public String getTransferNo() {
		return transferNo;
	}

	public void setTransferNo(String transferNo) {
		this.transferNo = transferNo;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getDateOfIssue() {
		return dateOfIssue;
	}

	public void setDateOfIssue(Long dateOfIssue) {
		this.dateOfIssue = dateOfIssue;
	}

	public String getDestinationIban() {
		return destinationIban;
	}

	public void setDestinationIban(String destinationIban) {
		this.destinationIban = destinationIban;
	}

	public String getAccountOwnerNationalCode() {
		return accountOwnerNationalCode;
	}

	public void setAccountOwnerNationalCode(String accountOwnerNationalCode) {
		this.accountOwnerNationalCode = accountOwnerNationalCode;
	}

	public String getAccountOwnerName() {
		return accountOwnerName;
	}

	public void setAccountOwnerName(String accountOwnerName) {
		this.accountOwnerName = accountOwnerName;
	}

	public String getAccountOwnerPersonType() {
		return accountOwnerPersonType;
	}

	public void setAccountOwnerPersonType(String accountOwnerPersonType) {
		this.accountOwnerPersonType = accountOwnerPersonType;
	}

	public String getAccountOwnerMobileNumber() {
		return accountOwnerMobileNumber;
	}

	public void setAccountOwnerMobileNumber(String accountOwnerMobileNumber) {
		this.accountOwnerMobileNumber = accountOwnerMobileNumber;
	}

	public String getTransferOwnerNationalCode() {
		return transferOwnerNationalCode;
	}

	public void setTransferOwnerNationalCode(String transferOwnerNationalCode) {
		this.transferOwnerNationalCode = transferOwnerNationalCode;
	}

	public String getTransferOwnerName() {
		return transferOwnerName;
	}

	public void setTransferOwnerName(String transferOwnerName) {
		this.transferOwnerName = transferOwnerName;
	}

	public Integer getChecksum() {
		return checksum;
	}

	public void setChecksum(Integer checksum) {
		this.checksum = checksum;
	}

	public String getTranserDescriprion() {
		return transerDescriprion;
	}

	public void setTranserDescriprion(String transerDescriprion) {
		this.transerDescriprion = transerDescriprion;
	}

	public String getTransferPayId() {
		return transferPayId;
	}

	public void setTransferPayId(String transferPayId) {
		this.transferPayId = transferPayId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<FirstSignerCandidateEntity> getFirstSignerCandidateEntities() {
		return firstSignerCandidateEntities;
	}

	public void setFirstSignerCandidateEntities(List<FirstSignerCandidateEntity> firstSignerCandidateEntities) {
		this.firstSignerCandidateEntities = firstSignerCandidateEntities;
	}

	public List<SecondSignerCandidateEntity> getSecondSignerCandidateEntities() {
		return secondSignerCandidateEntities;
	}

	public void setSecondSignerCandidateEntities(List<SecondSignerCandidateEntity> secondSignerCandidateEntities) {
		this.secondSignerCandidateEntities = secondSignerCandidateEntities;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
