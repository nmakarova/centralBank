package entities;

import java.io.Serializable;
import java.math.BigDecimal;

import utils.UUIDGenerator;

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

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

}
