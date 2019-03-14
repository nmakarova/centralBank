package com.centralbank.entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class CorrespondentAccount extends PersonalAccount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6032689975561255371L;

	String corrBankUuid;

	public CorrespondentAccount(BigDecimal availableAmount, String bankUuid, String corrBankUuid) {
		this(availableAmount, bankUuid);
		this.corrBankUuid = corrBankUuid;
	}

	private CorrespondentAccount(BigDecimal availableAmount, String bankUuid) {
		super(availableAmount, bankUuid);
	}

	public String getCorrBankUuid() {
		return corrBankUuid;
	}

	public void setCorrBankUuid(String corrBankUuid) {
		this.corrBankUuid = corrBankUuid;
	}

}
