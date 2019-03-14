package com.centralbank.services;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.centralbank.entities.DocumentStatus;
import com.centralbank.entities.PaymentDocument;
import com.centralbank.utils.BaseModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PaymentTransportCallable implements Serializable, Callable<DocumentStatus> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5138623814977504898L;
	
	PaymentDocument document;

	public PaymentTransportCallable(PaymentDocument document) {
		this.document = document;
	}

	public DocumentStatus call() throws Exception {
		Injector injector = Guice.createInjector(new BaseModule());
		BankService bankService = injector.getInstance(BankService.class);
		return bankService.executePaymentDocument(document);
	}
}
