package com.centralbank.utils;

import com.centralbank.services.BankService;
import com.centralbank.services.HazelcastInstanceService;
import com.google.inject.AbstractModule;

public class BaseModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(BankService.class);
		bind(HazelcastInstanceService.class);
	}
}
