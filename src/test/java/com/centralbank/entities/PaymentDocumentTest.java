package com.centralbank.entities;

import java.math.BigDecimal;

import com.centralbank.entities.PaymentDocument;
import com.centralbank.utils.UUIDGenerator;

import junit.framework.TestCase;

public class PaymentDocumentTest extends TestCase {

	public void testTransferAmount() {
		BigDecimal transferAmount = new BigDecimal(537);
		String debitAccountUuid = UUIDGenerator.generateUuid();
		String creditAccountUuid = UUIDGenerator.generateUuid();
		PaymentDocument paymentDocument = new PaymentDocument(debitAccountUuid, creditAccountUuid, transferAmount);
		assertEquals(transferAmount, paymentDocument.getTransferAmount());
	}

}
