package centralBank.centralBank;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
	}
	
	public static void guiceInjection() {
		Injector injector = Guice.createInjector(new BaseModule());
		bankService = injector.getInstance(BankService.class);
	}
}
