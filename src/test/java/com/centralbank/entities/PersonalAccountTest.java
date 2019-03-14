package com.centralbank.entities;

import java.math.BigDecimal;

import com.centralbank.entities.PersonalAccount;
import com.centralbank.utils.UUIDGenerator;

import junit.framework.TestCase;

public class PersonalAccountTest extends TestCase {

	public void testAvailableAmount() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = UUIDGenerator.generateUuid();
		PersonalAccount personalAccount = new PersonalAccount(availableAmount, bankUUID);
		assertEquals(availableAmount, personalAccount.getAvailableAmount());
	}

	public void testUUID() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = UUIDGenerator.generateUuid();
		PersonalAccount personalAccount = new PersonalAccount(availableAmount, bankUUID);
		assertEquals(personalAccount.getUuid().substring(0, personalAccount.getUuid().indexOf(".")), bankUUID);
	}

}
