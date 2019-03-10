package centralBank.centralBank;

import java.math.BigDecimal;

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
		App.main(null);
		PersonalAccount personalAccount = App.bankService.createPersonalAccount(new BigDecimal(456));
		assertEquals(personalAccount.getUuid().substring(0, personalAccount.getUuid().indexOf(".")),
				App.bankService.getBankUUID());
	}
}
