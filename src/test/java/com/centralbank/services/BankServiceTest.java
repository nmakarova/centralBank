package com.centralbank.services;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.centralbank.services.BankService;

import junit.framework.TestCase;

public class BankServiceTest extends TestCase {
	private static final Logger LOG = LogManager.getLogger(BankServiceTest.class);
	
	public void testPersonalAccountsCreationFromFile() {
		BankService bankService = new BankService();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("services//bank1.txt").getFile());
		String filePath = file.getAbsolutePath();
		try {
			bankService.readPersonalAccountFromFile(filePath);
		} catch (FileNotFoundException e) {
			LOG.error(e);
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
