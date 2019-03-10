package entities;

import java.math.BigDecimal;
import java.util.UUID;

import junit.framework.TestCase;

public class PersonalAccountTest extends TestCase {

	public void testAvailableAmount() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = generateUuid();
		PersonalAccount personalAccount = new PersonalAccount(availableAmount, bankUUID);
		assertEquals(availableAmount, personalAccount.getAvailableAmount());
	}
	
	public void testUUID() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = generateUuid();
		PersonalAccount personalAccount = new PersonalAccount(availableAmount, bankUUID);
		assertEquals(personalAccount.getUuid().substring(0, personalAccount.getUuid().indexOf(".")), bankUUID);
	}

	private String generateUuid() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
	}

}
