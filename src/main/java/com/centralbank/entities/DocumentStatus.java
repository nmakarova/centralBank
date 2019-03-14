package com.centralbank.entities;

import java.io.Serializable;

public enum DocumentStatus implements Serializable {
	CREATED(1), ON_PERFOMANCE(2), EXECUTED(3), NOT_ENOUGH_MONEY_ON_CLIENT_CREDIT(4), NOT_ENOUGH_MONEY_ON_CORR_CREDIT(5),
	NOT_ENOUGH_MONEY_ON_CORR_DEBIT(6), REJECTED(7), NO_SUCH_BANK(8);

	private int code;

	DocumentStatus(int code) {
		this.code = code;
	}

	public int code() {
		return code;
	}

}
