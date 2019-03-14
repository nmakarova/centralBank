package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hazelcast.core.Member;

import entities.CorrespondentAccount;
import entities.DocumentStatus;
import entities.PaymentDocument;
import entities.PersonalAccount;
import utils.UUIDGenerator;

@Singleton
public class BankService {

	public static final String BANK_UUID = UUIDGenerator.generateUuid();

	@Inject
	private HazelcastInstanceService hzService;

	private static Map<String, PersonalAccount> personalAccounts = new HashMap<String, PersonalAccount>();
	Map<String, List<CorrespondentAccount>> correspondentAccounts;
	Map<String, List<String>> corrAccounstUuids;

	public String getBankUUID() {
		return BANK_UUID;
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
			List<CorrespondentAccount> corrAccounts = getCorrespondentAccounts().get(bankUUID);
			corrAccounts.add(correspondentAccount);
			getCorrespondentAccounts().put(bankUUID, corrAccounts);
		} else {
			List<CorrespondentAccount> corrAccounts = new ArrayList<CorrespondentAccount>();
			corrAccounts.add(correspondentAccount);
			getCorrespondentAccounts().put(bankUUID, corrAccounts);
		}
	}

	public void addToCorrespondentAccountsUuids(String bankUUID, String corrBankUUID) {
		if (getCorrespondentAccountsUuids().containsKey(bankUUID)) {
			List<String> corrAccountsUuids = getCorrespondentAccountsUuids().get(bankUUID);
			corrAccountsUuids.add(corrBankUUID);
			getCorrespondentAccountsUuids().put(bankUUID, corrAccountsUuids);
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
				BigDecimal availableAmount = BigDecimal.valueOf(ammountFromFile);
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
	
	public CorrespondentAccount getCorrespondentAccountByUuid(String uuid) {
		CorrespondentAccount result = null;
		List<CorrespondentAccount> corrAccounts = hzService.getCorrespondentAccounts().get(getBankUUID());
		for(CorrespondentAccount corrAccount : corrAccounts) {
			if(corrAccount.getCorrBankUuid().equals(uuid)) {
				result = corrAccount;
				break;
			}
		}
		return result;
	}

	public void addNewBankCorrecpondentAccounts(Map<String, BigDecimal> corrAccountsInfo) {
		List<String> corrAccountsUuids = new ArrayList<String>();
		List<CorrespondentAccount> corrAccounts = new ArrayList<CorrespondentAccount>();
		if (corrAccountsInfo == null) {
			getCorrespondentAccountsUuids().put(getBankUUID(), corrAccountsUuids);
			getCorrespondentAccounts().put(getBankUUID(), corrAccounts);
			return;
		}
		Set <Member> hzMembers = hzService.getInstance().getCluster().getMembers();
		for (Member member : hzMembers) {
			if (!member.equals(hzService.getInstance().getCluster().getLocalMember())) {
				String corrBankUUID = member.getStringAttribute("bankUUID");
				corrAccountsUuids.add(corrBankUUID);
				BigDecimal availableAmount = corrAccountsInfo.get(corrBankUUID);
				CorrespondentAccount corrAccountThisBank = new CorrespondentAccount(availableAmount, getBankUUID(),
						corrBankUUID);
				corrAccounts.add(corrAccountThisBank);
				CorrespondentAccount corrAccountOtherBank = new CorrespondentAccount(availableAmount, corrBankUUID,
						getBankUUID());
				addToCorrespondentAccountsUuids(corrBankUUID, getBankUUID());
				addToCorrespondentAccounts(corrBankUUID, corrAccountOtherBank);
			}
		}
		getCorrespondentAccountsUuids().put(getBankUUID(), corrAccountsUuids);
		getCorrespondentAccounts().put(getBankUUID(), corrAccounts);
	}

	public PaymentDocument perfomPaymentDocument(PaymentDocument document) {
		document.setStatus(DocumentStatus.ON_PERFOMANCE);
		boolean isCreditAccountBelongsToThisBank = isAccountBelongsToThisBank(document.getCreditAccountUuid());
		boolean isDebitAccountBelongsToThisBank = isAccountBelongsToThisBank(document.getDebitAccountUuid());
		if (isDebitAccountBelongsToThisBank && isCreditAccountBelongsToThisBank) {
			DocumentStatus result = executeLocalPay(document);
			document.setStatus(result);
		} else if (isCreditAccountBelongsToThisBank) {
			executePayFromOurCreditToCorrDebit(document);
			String bankUuid = PersonalAccount.getBankUuid(document.getDebitAccountUuid());
			DocumentStatus result = hzService.sendPaymentToAnotherBank(document, bankUuid);
			document.setStatus(result);
			if (!result.equals(DocumentStatus.EXECUTED)) {
				rejectFromOurCreditToCorrDebit(document);
			}
		}
		return document;
	}

	public DocumentStatus executePaymentDocument(PaymentDocument document) {
		DocumentStatus result = DocumentStatus.REJECTED;
		document.setStatus(DocumentStatus.ON_PERFOMANCE);
		boolean isDebitAccountBelongsToThisBank = isAccountBelongsToThisBank(document.getDebitAccountUuid());
		boolean isCreditAccountBelongsToThisBank = isAccountBelongsToThisBank(document.getCreditAccountUuid());
		if (isDebitAccountBelongsToThisBank && isCreditAccountBelongsToThisBank) {
			result = executeLocalPay(document);
			document.setStatus(result);
		} else if (isDebitAccountBelongsToThisBank) {
			result = executePayToOurDebit (document);
		}
		return result;
	}
	
	private boolean isAccountBelongsToThisBank(String accountUUID) {
		return getBankUUID().equals(PersonalAccount.getBankUuid(accountUUID));
	}

	private DocumentStatus executeLocalPay(PaymentDocument document) {
		if (!isEnoughMoneyOnCredit(document)) {
			return DocumentStatus.NOT_ENOUGH_MONEY_ON_CLIENT_CREDIT;
		} else {
			synchronized (getPersonalAccounts()) {
				PersonalAccount creditAccount = getPersonalAccount(document.getCreditAccountUuid());
				PersonalAccount debitAccount = getPersonalAccount(document.getDebitAccountUuid());
				creditAccount.setAvailableAmount(creditAccount.getAvailableAmount().subtract(document.getTransferAmount()));
				debitAccount.setAvailableAmount(debitAccount.getAvailableAmount().add(document.getTransferAmount()));
			}
		}
		return DocumentStatus.EXECUTED;
	}
	
	private DocumentStatus executePayToOurDebit(PaymentDocument document) {
		if (!isEnoughMoneyOnDebCorrAccount(document)) {
			return DocumentStatus.NOT_ENOUGH_MONEY_ON_CORR_CREDIT;
		} else {
			synchronized (getPersonalAccounts()) {
				String corAccountUuid = PersonalAccount.getBankUuid(document.getCreditAccountUuid());
				CorrespondentAccount corrAccount = getCorrespondentAccountByUuid(corAccountUuid);
				PersonalAccount debitAccount = getPersonalAccount(document.getDebitAccountUuid());
				corrAccount.setAvailableAmount(corrAccount.getAvailableAmount().subtract(document.getTransferAmount()));
				debitAccount.setAvailableAmount(debitAccount.getAvailableAmount().add(document.getTransferAmount()));
			}
		}
		return DocumentStatus.EXECUTED;
	}
	
	private DocumentStatus executePayFromOurCreditToCorrDebit(PaymentDocument document) {
		if (!isEnoughMoneyOnCredit(document)) {
			return DocumentStatus.NOT_ENOUGH_MONEY_ON_CLIENT_CREDIT;
		} else {
			synchronized (getPersonalAccounts()) {
				PersonalAccount creditAccount = getPersonalAccount(document.getCreditAccountUuid());
				String corAccountUuid = PersonalAccount.getBankUuid(document.getDebitAccountUuid());
				CorrespondentAccount corrAccount = getCorrespondentAccountByUuid(corAccountUuid);
				creditAccount.setAvailableAmount(creditAccount.getAvailableAmount().subtract(document.getTransferAmount()));
				corrAccount.setAvailableAmount(corrAccount.getAvailableAmount().add(document.getTransferAmount()));
			}
		}
		return DocumentStatus.EXECUTED;
	}
	
	private DocumentStatus rejectFromOurCreditToCorrDebit(PaymentDocument document) {
		if (!isEnoughMoneyOnCredCorrAccount(document)) {
			return DocumentStatus.NOT_ENOUGH_MONEY_ON_CORR_DEBIT;
		} else {
			synchronized (getPersonalAccounts()) {
				PersonalAccount creditAccount = getPersonalAccount(document.getCreditAccountUuid());
				String corAccountUuid = PersonalAccount.getBankUuid(document.getDebitAccountUuid());
				CorrespondentAccount corrAccount = getCorrespondentAccountByUuid(corAccountUuid);
				creditAccount.setAvailableAmount(creditAccount.getAvailableAmount().add(document.getTransferAmount()));
				corrAccount.setAvailableAmount(corrAccount.getAvailableAmount().subtract(document.getTransferAmount()));
			}
		}
		return DocumentStatus.EXECUTED;
	}

	private boolean isEnoughMoneyOnCredit(PaymentDocument document) {
		PersonalAccount creditAccount = getPersonalAccount(document.getCreditAccountUuid());
		Integer compareDecimalsResult = creditAccount.getAvailableAmount().compareTo(document.getTransferAmount());
		return (compareDecimalsResult.equals(1)) || (compareDecimalsResult.equals(0));
	}
	
	private boolean isEnoughMoneyOnDebCorrAccount(PaymentDocument document) {
		String corAccountUuid = PersonalAccount.getBankUuid(document.getCreditAccountUuid());
		CorrespondentAccount corrAccount = getCorrespondentAccountByUuid(corAccountUuid);
		Integer compareDecimalsResult = corrAccount.getAvailableAmount().compareTo(document.getTransferAmount());
		return (compareDecimalsResult.equals(1)) || (compareDecimalsResult.equals(0));
	}
	
	private boolean isEnoughMoneyOnCredCorrAccount(PaymentDocument document) {
		String corAccountUuid = PersonalAccount.getBankUuid(document.getDebitAccountUuid());
		CorrespondentAccount corrAccount = getCorrespondentAccountByUuid(corAccountUuid);
		Integer compareDecimalsResult = corrAccount.getAvailableAmount().compareTo(document.getTransferAmount());
		return (compareDecimalsResult.equals(1)) || (compareDecimalsResult.equals(0));
	}
}
