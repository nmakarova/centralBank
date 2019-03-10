package entities;

import java.math.BigDecimal;
import java.util.UUID;

import junit.framework.TestCase;

public class CorrespondentAccountTest extends TestCase {

	public void testAvailableAmount() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = generateUuid();
		String corrBankUUID = generateUuid();
		CorrespondentAccount corrAccount = new CorrespondentAccount(availableAmount, bankUUID, corrBankUUID);
		assertEquals(availableAmount, corrAccount.getAvailableAmount());
	}

	public void testUUID() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = generateUuid();
		String corrBankUUID = generateUuid();
		CorrespondentAccount corrAccount = new CorrespondentAccount(availableAmount, bankUUID, corrBankUUID);
		assertEquals(corrAccount.getUuid().substring(0, corrAccount.getUuid().indexOf(".")), bankUUID);
	}

	public void testCorrBankUUID() {
		BigDecimal availableAmount = new BigDecimal(537);
		String bankUUID = generateUuid();
		String corrBankUUID = generateUuid();
		CorrespondentAccount corrAccount = new CorrespondentAccount(availableAmount, bankUUID, corrBankUUID);
		assertEquals(corrAccount.corrBankUuid, corrBankUUID);
	}

	private String generateUuid() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
	}

}
