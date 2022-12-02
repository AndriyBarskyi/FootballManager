package com.task.test.footballmanager.exception;

import javax.persistence.PersistenceException;

public class InsufficientFundsException extends PersistenceException {
    private static final String INSUFFICIENT_FUNDS =
        "Insufficient funds to commit the transfer";

    public InsufficientFundsException(String message) {
        super(message.isEmpty() ? INSUFFICIENT_FUNDS : message);
    }

    public InsufficientFundsException() {
        super(INSUFFICIENT_FUNDS);
    }
}
