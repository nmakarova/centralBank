package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import entities.CorrespondentAccount;
import entities.PersonalAccount;
import utils.UUIDGenerator;

@Singleton
public class BankService {

	public static String bankUUID = UUIDGenerator.generateUuid();;

	@Inject
	private HazelcastInstanceService hzService;

	Map<String, PersonalAccount> personalAccounts;
	Map<String, List<CorrespondentAccount>> correspondentAccounts;
	Map<String, List<String>> corrAccounstUuids;

	@Inject
	public BankService() {
		personalAccounts = new ConcurrentHashMap<String, PersonalAccount>();
	}

	public String getBankUUID() {
		return bankUUID;
	}

	public Map<String, PersonalAccount> getPersonalAccounts() {
		return personalAccounts;
	}

	public void clearPersonalAccounts() {
		getPersonalAccounts().clear();
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

	public void addToCorrespondentAccounts(String bankUUID, CorrespondentAccount correspondentAccount) {
		if (getCorrespondentAccounts().containsKey(bankUUID)) {
			getCorrespondentAccounts().get(bankUUID).add(correspondentAccount);
		} else {
			List<CorrespondentAccount> corrAccounts = new ArrayList<CorrespondentAccount>();
			corrAccounts.add(correspondentAccount);
			getCorrespondentAccounts().put(bankUUID, corrAccounts);
		}
	}

	public void addToCorrespondentAccountsUuids(String bankUUID, String corrBankUUID) {
		if (getCorrespondentAccountsUuids().containsKey(bankUUID)) {
			getCorrespondentAccountsUuids().get(bankUUID).add(corrBankUUID);
		} else {
			List<String> corrAccountsUuids = new ArrayList<String>();
			corrAccountsUuids.add(corrBankUUID);
			getCorrespondentAccountsUuids().put(bankUUID, corrAccountsUuids);
		}
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

	public Map<String, List<String>> getCorrespondentAccountsUuids() {
		return hzService.getCorrespondentAccountsUuids();
	}

	public Map<String, List<CorrespondentAccount>> getCorrespondentAccounts() {
		return hzService.getCorrespondentAccounts();
	}

	public void addNewBankCorrecpondentAccounts(Map<String, BigDecimal> corrAccountsInfo) {
		List<String> corrAccountsUuids = new ArrayList<String>();
		List<CorrespondentAccount> corrAccounts = new ArrayList<CorrespondentAccount>();
		if (corrAccountsInfo == null) {
			getCorrespondentAccountsUuids().put(getBankUUID(), corrAccountsUuids);
			getCorrespondentAccounts().put(getBankUUID(), corrAccounts);
			return;
		}
		for (HazelcastInstance hzInstance : Hazelcast.getAllHazelcastInstances()) {
			if (!hzInstance.equals(hzService.getInstance())) {
				String corrBankUUID = hzInstance.getConfig().getProperty("bankUUID");
				corrAccountsUuids.add(corrBankUUID);
				String bankUUID = getBankUUID();
				BigDecimal availableAmount = corrAccountsInfo.get(corrBankUUID);
				CorrespondentAccount corrAccountThisBank = new CorrespondentAccount(availableAmount, bankUUID,
						corrBankUUID);
				corrAccounts.add(corrAccountThisBank);
				CorrespondentAccount corrAccountOtherBank = new CorrespondentAccount(availableAmount, corrBankUUID,
						bankUUID);
				addToCorrespondentAccountsUuids(corrBankUUID, bankUUID);
				addToCorrespondentAccounts(corrBankUUID, corrAccountOtherBank);
			}
		}
		getCorrespondentAccountsUuids().put(getBankUUID(), corrAccountsUuids);
		getCorrespondentAccounts().put(getBankUUID(), corrAccounts);
	}
}
