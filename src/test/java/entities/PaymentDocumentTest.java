package entities;

import java.math.BigDecimal;
import java.util.UUID;

import junit.framework.TestCase;

public class PaymentDocumentTest extends TestCase {

	public void testTransferAmount() {
		BigDecimal transferAmount = new BigDecimal(537);
		String debitAccountUuid = generateUuid();
		String creditAccountUuid = generateUuid();
		PaymentDocument paymentDocument = new PaymentDocument(debitAccountUuid, creditAccountUuid, transferAmount);
		assertEquals(transferAmount, paymentDocument.getTransferAmount());
	}

	private String generateUuid() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
	}

}
