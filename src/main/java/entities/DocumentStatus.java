package entities;

import java.io.Serializable;

public enum DocumentStatus implements Serializable{
	CREATED(1),
	ON_PERFOMANCE(1),
	EXECUTED(2),
	NOT_ENOUGH_MONEY_ON_CLIENT_CREDIT(3),
	NOT_ENOUGH_MONEY_ON_CORR_CREDIT(4),
	NOT_ENOUGH_MONEY_ON_CORR_DEBIT(5),
	REJECTED(6),
	NO_SUCH_BANK(7);
	
	private int code;

	DocumentStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
	
}
