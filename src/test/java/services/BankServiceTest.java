package services;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

public class BankServiceTest extends TestCase {

	public void testPersonalAccountsCreationFromFile() {
		BankService bankService = new BankService();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("services//bank1.txt").getFile());
		String filePath = file.getAbsolutePath();
		try {
			bankService.readPersonalAccountFromFile(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(9, bankService.getPersonalAccounts().size());
	}

	public void testPersonalAccountsCreationFromFileNoSuchFileEx() {
		BankService bankService = new BankService();
		try {
			bankService.readPersonalAccountFromFile("bank.txt");
		} catch (FileNotFoundException e) {
			assertEquals("bank.txt (Не удается найти указанный файл)", e.getMessage());
		}
	}

}
