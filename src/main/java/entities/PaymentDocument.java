package entities;

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
	
	public PaymentDocument(String debitAccountUuid, String creditAccountUuid, BigDecimal transfeAmount) {
		super();
		this.debitAccountUuid = debitAccountUuid;
		this.creditAccountUuid = creditAccountUuid;
		this.transferAmount = transfeAmount;
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
		
}