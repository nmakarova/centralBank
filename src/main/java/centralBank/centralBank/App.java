package centralBank.centralBank;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;

import entities.PaymentDocument;
import entities.PersonalAccount;
import services.BankService;
import services.HazelcastInstanceService;
import utils.BaseModule;

/**
 * main class
 *
 */
public class App {

	static BankService bankService;
	static HazelcastInstanceService hzService;

	public static void main(String[] args) {
		guiceInjection();
		hzService.createHazelcasInstance();
		Scanner in = new Scanner(System.in);
		Map<String, BigDecimal> corrMapInfo = getCorrAccountsInfo(in);
		bankService.addNewBankCorrecpondentAccounts(corrMapInfo);
		String command = "";
		System.out.println("Welcome to our bank!");
		System.out.println("How can we help you?");
		System.out.print("Put you message here: ");
		while (!command.equals(Commands.CLOSE.name())) {
			command = in.nextLine().toUpperCase();
			executeCommand(command, in);
			if (!command.equals(Commands.CLOSE.name())) {
				System.out.println("Anything else?");
				System.out.print("Put you message here: ");
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
			System.out.println("Let's open correspondent Accounts !");
			for (String bankUuid : hzService.getCorrespondentAccountsUuids().keySet()) {
				int tryCount = 0;
				boolean success = false;
				BigDecimal amount = null;
				while (tryCount < 3 && !success) {
					System.out.println("Put corrAccount availableAmount (Ex: 100500.66666) for bank =  " + bankUuid);
					String amountString = in.nextLine().trim();
					try {
						amount = new BigDecimal(amountString);
					} catch (Exception e) {
						tryCount++;
						int attempsLeft = 3 - tryCount;
						if (attempsLeft != 0) {
							System.out.println("Try again. You have " + attempsLeft + " attemps left");
						}
						continue;
					}
					success = true;
				}
				if (success) {
					corrMapInfo.put(bankUuid, amount);
				} else {
					System.out.println("We are sorry you try to cheat us. We need to close now.");
					close(in);
				}
			}
			return corrMapInfo;
		}
		return null;
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
				executeShowAll(in);
				break;
			case PAYMENT:
				executePayment(in);
				break;
			default:
				System.out.println("Sorry, there is no such operation in our bank. Please try again!");
				break;
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Sorry, there is no such operation in our bank. Please try again!");
			return;
		}

	}

	public static void executeReadPC(Scanner in) {
		try {
			System.out.println("Say the absolute path to the file with accounts (Ex: C:\\Users\\bank1.txt)");
			System.out.print("Put you message here: ");
			String path = in.nextLine();
			bankService.readPersonalAccountFromFile(path);
		} catch (FileNotFoundException e) {
			System.out.println("Sorry, we can't read personalAccounts from this file. Please try again!");
			return;
		}
	}

	public static void executeShowAll(Scanner in) {
		try {
			System.out.println("All accounts of this bank:");
			Map<String, PersonalAccount> personalAccounts = bankService.getPersonalAccounts();
			for (Map.Entry<String, PersonalAccount> personalAccount : personalAccounts.entrySet()) {
				System.out.println(personalAccount.getValue());
			}
		} catch (Exception e) {
			System.out.println("Sorry, we can't show you all accounts of this bank.");
			return;
		}
	}
	
	public static void executePayment(Scanner in) {
		try {
			System.out.println("Let's create payment document!");
			System.out.println("Print full credit account uuid (Ex: c1af2.1ac13)");
			String creditAccountUuid = in.nextLine().trim();
			System.out.println("Print full debit account uuid (Ex: c1af2.1ac13)");
			String debitAccountUuid = in.nextLine().trim();
			int tryCount = 0;
			boolean success = false;
			BigDecimal amount = null;
			while (tryCount < 3 && !success) {
				System.out.println("Print amount to transfer (Ex: 450.444)");
				String amountString = in.nextLine().trim();
				try {
					amount = new BigDecimal(amountString);
				} catch (Exception e) {
					tryCount++;
					int attempsLeft = 3 - tryCount;
					if (attempsLeft != 0) {
						System.out.println("Try again. You have " + attempsLeft + " attemps left");
					}
					continue;
				}
				success = true;
			}
			if (success) {
				PaymentDocument document = new PaymentDocument(debitAccountUuid, creditAccountUuid, amount);
				bankService.perfomPaymentDocument(document);
			} else {
				System.out.println("Sorry, try again. ");
			}

		} catch (Exception e) {
			System.out.println("Sorry, we can't perfom you payment");
			return;
		}
	}

	public static void close(Scanner in) {
		System.out.println("Bank closed until tomorrow!");
		hzService.getInstance().shutdown();
		in.close();
		System.exit(0);
	}
}
