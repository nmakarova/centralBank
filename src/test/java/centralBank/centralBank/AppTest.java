package centralBank.centralBank;

import java.math.BigDecimal;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import entities.PersonalAccount;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testGuice() {
		App.guiceInjection();
		PersonalAccount personalAccount = App.bankService.createPersonalAccount(new BigDecimal(456));
		assertEquals(personalAccount.getUuid().substring(0, personalAccount.getUuid().indexOf(".")),
				App.bankService.getBankUUID());
	}

	public void testHazelcastCreation() {
		App.guiceInjection();
		App.hzService.createHazelcasInstance();
		HazelcastInstance instance = App.hzService.getInstance();
		assertEquals(instance.getConfig().getProperty("bankUUID"), App.bankService.getBankUUID());
		instance.shutdown();
	}
	
	public void testCreationSeveralHZInstances() {
		App.guiceInjection();
		App.hzService.createHazelcasInstance();
		App.hzService.createHazelcasInstance();
		assertEquals(2, Hazelcast.getAllHazelcastInstances().size());
		Hazelcast.shutdownAll();
	}

}
