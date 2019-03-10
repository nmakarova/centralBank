package entities;

import java.math.BigDecimal;

import junit.framework.TestCase;
import utils.UUIDGenerator;

public class PaymentDocumentTest extends TestCase {

	public void testTransferAmount() {
		BigDecimal transferAmount = new BigDecimal(537);
		String debitAccountUuid = UUIDGenerator.generateUuid();
		String creditAccountUuid = UUIDGenerator.generateUuid();
		PaymentDocument paymentDocument = new PaymentDocument(debitAccountUuid, creditAccountUuid, transferAmount);
		assertEquals(transferAmount, paymentDocument.getTransferAmount());
	}

}
