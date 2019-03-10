package entities;

import java.math.BigDecimal;

import junit.framework.TestCase;
import utils.UUIDGenerator;

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
