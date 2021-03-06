package com.centralbank.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import com.centralbank.utils.UUIDGenerator;

public class PersonalAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3578371901251931351L;

	String uuid;
	BigDecimal availableAmount;

	public PersonalAccount(BigDecimal availableAmount, String bankUuid) {
		super();
		this.availableAmount = availableAmount;
		this.uuid = bankUuid.concat(".").concat(UUIDGenerator.generateUuid());
	}

	public String getUuid() {
		return uuid;
	}

	public String getLocalUuid() {
		return uuid.substring(uuid.indexOf('.')+1);
	}
	
	public String getBankUuid() {
		return uuid.substring(0, uuid.indexOf('.'));
	}
	
	public static String getBankUuid(String uuid) {
		return uuid.substring(0, uuid.indexOf('.'));
	}
	
	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	@Override
	public String toString() {
		return "Account uuid = " + getUuid() + " AvailableAmount = " + availableAmount.doubleValue();
	}
}  
