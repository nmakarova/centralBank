package com.centralbank.console;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.centralbank.entities.PaymentDocument;
import com.centralbank.entities.PersonalAccount;
import com.centralbank.services.BankService;
import com.centralbank.services.HazelcastInstanceService;
import com.centralbank.utils.BaseModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * main class
 *
 */
public class App {
	private static final Logger LOG = LogManager.getLogger(App.class);
	static BankService bankService;
	static HazelcastInstanceService hzService;

	public static void main(String[] args) {
		guiceInjection();
		hzService.createHazelcasInstance();
		Scanner in = new Scanner(System.in);
		Map<String, BigDecimal> corrMapInfo = getCorrAccountsInfo(in);
		bankService.addNewBankCorrecpondentAccounts(corrMapInfo);
		String command = "";
		LOG.info("Welcome to our bank!");
		LOG.info("How can we help you?");
		while (!command.equals(Commands.CLOSE.name())) {
			LOG.info("Put you message here: ");
			command = in.nextLine().trim().toUpperCase();
			executeCommand(command, in);
			if (!command.equals(Commands.CLOSE.name())) {
				LOG.info("Anything else?");
			}
		}
		close(in);
	}

	public static void guiceInjection() {
		Injector injector = Guice.createInjector(new BaseModule());
		bankService = injector.getInstance(BankService.class);
		hzService = injector.getInstance(HazelcastInstanceService.class);
	}

	public static Map<String, BigDecimal> getCorrAccountsInfo(Scanner in) {
		Map<String, BigDecimal> corrMapInfo = new HashMap<String, BigDecimal>();
		if (!hzService.isOnlyOneInstance()) {
			LOG.info("Let's open correspondent Accounts !");
			enterCorrAccountsAmounts(corrMapInfo, in);
			return corrMapInfo;
		}
		return null;
	}

	private static void enterCorrAccountsAmounts(Map<String, BigDecimal> corrMapInfo, Scanner in) {
		for (String bankUuid : hzService.getCorrespondentAccountsUuids().keySet()) {
			int tryCount = 0;
			int maxRetry = 3;
			boolean success = false;
			BigDecimal amount = null;
			while (tryCount < maxRetry && !success) {
				LOG.info("Put corrAccount availableAmount (Ex: 100500.66666) for bank =  " + bankUuid);
				String amountString = in.nextLine().trim();
				try {
					amount = new BigDecimal(amountString);
				} catch (Exception e) {
					tryCount++;
					int attempsLeft = maxRetry - tryCount;
					if (attempsLeft != 0) {
						LOG.info("Try again. You have " + attempsLeft + " attemps left");
					}
					continue;
				}
				success = true;
			}
			if (success) {
				corrMapInfo.put(bankUuid, amount);
			} else {
				LOG.info("We are sorry you try to cheat us. We need to close now.");
				close(in);
			}
		}
	}

	public static void executeCommand(String command, Scanner in) {
		try {
			switch (Commands.valueOf(command)) {
			case CLOSE:
				break;
			case READPC:
				executeReadPC(in);
				break;
			case SHOWALL:
				executeShowAll();
				break;
			case PAYMENT:
				executePayment(in);
				break;
			case INFO:
				executeInfo();
				break;
			default:
				LOG.info("Sorry, there is no such operation in our bank. Please try again!");
				break;
			}
		} catch (IllegalArgumentException e) {
			LOG.info("Sorry, there is no such operation in our bank. Please try again!");
		}

	}

	public static void executeReadPC(Scanner in) {
		try {
			LOG.info("Say the absolute path to the file with accounts (Ex: C:\\Users\\bank1.txt)");
			LOG.info("Put you message here: ");
			String path = in.nextLine();
			bankService.readPersonalAccountFromFile(path);
		} catch (FileNotFoundException e) {
			LOG.info("Sorry, we can't read personalAccounts from this file. Please try again!");
		}
	}

	public static void executeShowAll() {
		try {
			LOG.info("All accounts of this bank:");
			Map<String, PersonalAccount> personalAccounts = bankService.getPersonalAccounts();
			for (Map.Entry<String, PersonalAccount> personalAccount : personalAccounts.entrySet()) {
				LOG.info(personalAccount.getValue());
			}
		} catch (Exception e) {
			LOG.info("Sorry, we can't show you all accounts of this bank.");
		}
	}

	public static void executePayment(Scanner in) {
		try {
			LOG.info("Let's create payment document!");
			LOG.info("Print full credit account uuid (Ex: c1af2.1ac13)");
			String creditAccountUuid = in.nextLine().trim();
			LOG.info("Print full debit account uuid (Ex: c1af2.1ac13)");
			String debitAccountUuid = in.nextLine().trim();
			int tryCount = 0;
			int maxRetry = 3;
			boolean success = false;
			BigDecimal amount = null;
			while (tryCount < maxRetry && !success) {
				LOG.info("Print amount to transfer (Ex: 450.444)");
				String amountString = in.nextLine().trim();
				amount = tryString2BigDecimal(amountString, tryCount);
				if(amount != null) {
					success = true;
				} else {
					tryCount++;
				}
			}
			if (success) {
				PaymentDocument document = new PaymentDocument(debitAccountUuid, creditAccountUuid, amount);
				bankService.addPaymentDocumentToQueue(document);
			} else {
				LOG.info("Sorry, try again. ");
			}
		} catch (Exception e) {
			LOG.info("Sorry, we can't perfom you payment");
		}
	}

	private static BigDecimal tryString2BigDecimal(String amountString, int tryCount) {
		BigDecimal amount = null;
		int maxRetry = 3;
		try {
			amount = new BigDecimal(amountString);
		} catch (Exception e) {
			tryCount++;
			int attempsLeft = maxRetry - tryCount;
			if (attempsLeft != 0) {
				LOG.info("Try again. You have " + attempsLeft + " attemps left");
			}
		}
		return amount;
	}

	public static void executeInfo() {
		for (Commands command : Commands.values()) {
			switch (command) {
			case CLOSE:
				LOG.info("CLOSE: Close bank");
				break;
			case READPC:
				LOG.info("READPC: Read personalCounts from file");
				break;
			case SHOWALL:
				LOG.info("SHOWALL: Show all personall counts");
				break;
			case PAYMENT:
				LOG.info("PAYMENT: Execute payment");
				break;
			case INFO:
				LOG.info("INFO: info");
				break;
			default:
				LOG.info("Unknown");
				break;
			}
		}
	}

	public static void close(Scanner in) {
		LOG.info("Bank closed until tomorrow!");
		hzService.getInstance().shutdown();
		in.close();
		System.exit(0);
	}
}
