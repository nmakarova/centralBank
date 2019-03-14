package com.centralbank.entities;

import java.math.BigDecimal;

import com.centralbank.entities.CorrespondentAccount;
import com.centralbank.utils.UUIDGenerator;

import junit.framework.TestCase;

public class CorrespondentAccountTest extends TestCase {

	public void testAvailableAmount() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = UUIDGenerator.generateUuid();
		String corrBankUUID = UUIDGenerator.generateUuid();
		CorrespondentAccount corrAccount = new CorrespondentAccount(availableAmount, bankUUID, corrBankUUID);
		assertEquals(availableAmount, corrAccount.getAvailableAmount());
	}

	public void testUUID() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = UUIDGenerator.generateUuid();
		String corrBankUUID = UUIDGenerator.generateUuid();
		CorrespondentAccount corrAccount = new CorrespondentAccount(availableAmount, bankUUID, corrBankUUID);
		assertEquals(corrAccount.getUuid().substring(0, corrAccount.getUuid().indexOf(".")), bankUUID);
	}

	public void testCorrBankUUID() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = UUIDGenerator.generateUuid();
		String corrBankUUID = UUIDGenerator.generateUuid();
		CorrespondentAccount corrAccount = new CorrespondentAccount(availableAmount, bankUUID, corrBankUUID);
		assertEquals(corrAccount.corrBankUuid, corrBankUUID);
	}

}
