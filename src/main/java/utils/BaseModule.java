package utils;

import com.google.inject.AbstractModule;

import services.BankService;
import services.HazelcastInstanceService;

public class BaseModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(BankService.class);
		bind(HazelcastInstanceService.class);
	}
}
