package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

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
		this.uuid = bankUuid.concat(".").concat(generateUuid());
	}

	private String generateUuid() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
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
