package services;

import java.io.File;

import junit.framework.TestCase;

public class BankServiceTest extends TestCase {

	public void testPersonalAccountsCreationFromFile() {
		BankService bankService = new BankService();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("services//bank1.txt").getFile());
		String filePath = file.getAbsolutePath();
		bankService.readPersonalAccountFromFile(filePath);
		assertEquals(9, bankService.getPersonalAccounts().size());
	}

	
}
