package utils;

import com.google.inject.AbstractModule;

import services.BankService;

public class BaseModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(BankService.class);
	}
}
