package services;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import entities.CorrespondentAccount;

@Singleton
public class HazelcastInstanceService {

	@Inject
	BankService bankService;

	private HazelcastInstance instance;

	public void createHazelcasInstance() {
		Config cfg = new Config();
		cfg.setProperty("bankUUID", bankService.getBankUUID());
		cfg.setProperty("hazelcast.logging.type", "none");
		instance = Hazelcast.newHazelcastInstance(cfg);
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
}
