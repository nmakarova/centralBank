package services;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;

import entities.CorrespondentAccount;
import entities.DocumentStatus;
import entities.PaymentDocument;

@Singleton
public class HazelcastInstanceService {

	@Inject
	BankService bankService;

	private static HazelcastInstance instance;

	public void createHazelcasInstance() {
		Config cfg = new Config();
		cfg.setProperty("bankUUID", bankService.getBankUUID());
		cfg.setProperty("hazelcast.logging.type", "none");
		instance = Hazelcast.newHazelcastInstance(cfg);
		instance.getCluster().getLocalMember().setStringAttribute("bankUUID", bankService.getBankUUID());
	}

	public HazelcastInstance getInstance() {
		return instance;
	}

	public Boolean isOnlyOneInstance() {
		return getInstance().getCluster().getMembers().size() > 1 ? false : true;
	}

	public Map<String, List<CorrespondentAccount>> getCorrespondentAccounts() {
		return instance.getReplicatedMap("correspondentAccounts");
	}

	public Map<String, List<String>> getCorrespondentAccountsUuids() {
		return instance.getReplicatedMap("correspondentAccountsUuids");
	}
	
	public DocumentStatus sendPaymentToAnotherBank(PaymentDocument document, String bankUuid) {
		DocumentStatus result;
		Member member = findAnoutherBankHZMember(bankUuid);
		if (member == null) {
			return DocumentStatus.NO_SUCH_BANK;
		} else {
			PaymentTransportCallable transferDocument = new PaymentTransportCallable(document);
			IScheduledExecutorService executor = instance.getScheduledExecutorService("executor");
			IScheduledFuture<DocumentStatus> resultFuture = executor.scheduleOnMember(transferDocument, member, 0, TimeUnit.SECONDS);
			try {
				result = resultFuture.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return DocumentStatus.REJECTED;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return DocumentStatus.REJECTED;
			}
		}
		return result;
	}
	
	public Member findAnoutherBankHZMember(String bankUuid) {
		Member result = null;
		Set<Member> membersOfCluster = instance.getCluster().getMembers();
		for (Member member : membersOfCluster) {
			if (member.getStringAttribute("bankUUID").equals(bankUuid)) {
				result = member;
				break;
			}
		}
		return result;
	}
}
