package centralBank.centralBank;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import entities.PersonalAccount;
import services.BankService;
import utils.BaseModule;

/**
 * main class
 *
 */
public class App {

	static BankService bankService;

	public static void main(String[] args) {
		guiceInjection();
		createHazelcastInstance();
		Scanner in = new Scanner(System.in);
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
		System.out.println("Bank closed until tomorrow!");
		in.close();
	}

	public static void guiceInjection() {
		Injector injector = Guice.createInjector(new BaseModule());
		bankService = injector.getInstance(BankService.class);
	}

	public static HazelcastInstance createHazelcastInstance() {
		Config cfg = new Config();
		cfg.setProperty("bankUUID", bankService.getBankUUID());
		HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
		return instance;
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
			for(Map.Entry<String, PersonalAccount> personalAccount : personalAccounts.entrySet()) {
				System.out.println(personalAccount.getValue());
			}
		} catch (Exception e) {
			System.out.println("Sorry, we can't show you all accounts of this bank.");
			return;
		}
	}

}
