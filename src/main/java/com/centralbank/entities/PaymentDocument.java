package com.centralbank.entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentDocument implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3397222108056331469L;

	String debitAccountUuid;
	String creditAccountUuid;
	BigDecimal transferAmount;
	DocumentStatus status;

	public PaymentDocument(String debitAccountUuid, String creditAccountUuid, BigDecimal transfeAmount) {
		super();
		this.debitAccountUuid = debitAccountUuid;
		this.creditAccountUuid = creditAccountUuid;
		this.transferAmount = transfeAmount;
		status = DocumentStatus.CREATED;
	}

	public String getDebitAccountUuid() {
		return debitAccountUuid;
	}

	public void setDebitAccountUuid(String debitAccountUuid) {
		this.debitAccountUuid = debitAccountUuid;
	}

	public String getCreditAccountUuid() {
		return creditAccountUuid;
	}

	public void setCreditAccountUuid(String creditAccountUuid) {
		this.creditAccountUuid = creditAccountUuid;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public DocumentStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentStatus status) {
		this.status = status;
	}

}
