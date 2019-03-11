package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import entities.CorrespondentAccount;
import entities.PersonalAccount;
import utils.UUIDGenerator;

@Singleton
public class BankService {

	public static String bankUUID = UUIDGenerator.generateUuid();;

	Map<String, PersonalAccount> personalAccounts;
	Map<String, CorrespondentAccount> correspondentAccounts;

	@Inject
	public BankService() {
		personalAccounts = new ConcurrentHashMap<String, PersonalAccount>();
		correspondentAccounts = new ConcurrentHashMap<String, CorrespondentAccount>();
	}

	public String getBankUUID() {
		return bankUUID;
	}

	public Map<String, PersonalAccount> getPersonalAccounts() {
		return personalAccounts;
	}

	public Map<String, CorrespondentAccount> getCorrespondentAccounts() {
		return correspondentAccounts;
	}

	public void clearPersonalAccounts() {
		getPersonalAccounts().clear();
	}

	public void clearCorrespondentAccounts() {
		getCorrespondentAccounts().clear();
	}

	public Boolean personalAccountExist(String accountUUID) {
		return personalAccounts.containsKey(accountUUID);
	}

	public PersonalAccount getPersonalAccount(String accountUUID) {
		return personalAccounts.get(accountUUID);
	}

	public void addPersonalAccount(PersonalAccount personalAccount) {
		personalAccounts.put(personalAccount.getUuid(), personalAccount);
	}

	public PersonalAccount createPersonalAccount(BigDecimal availableAmount) {
		PersonalAccount personalAccount = new PersonalAccount(availableAmount, getBankUUID());
		addPersonalAccount(personalAccount);
		return personalAccount;
	}

	public void readPersonalAccountFromFile(String filePath) throws FileNotFoundException {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(" ");
			while (scanner.hasNext()) {
				Double ammountFromFile = scanner.nextDouble();
				BigDecimal availableAmount = new BigDecimal(ammountFromFile);
				createPersonalAccount(availableAmount);
			}
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

	}
}
