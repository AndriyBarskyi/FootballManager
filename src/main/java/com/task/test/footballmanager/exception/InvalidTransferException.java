package com.task.test.footballmanager.exception;

import javax.persistence.PersistenceException;

public class InvalidTransferException extends PersistenceException {
    private static final String CANNOT_COMMIT_THE_TRANSFER =
        "Cannot commit the transfer";

    public InvalidTransferException(String message) {
        super(message.isEmpty() ? CANNOT_COMMIT_THE_TRANSFER : message);
    }

    public InvalidTransferException() {
        super(CANNOT_COMMIT_THE_TRANSFER);
    }
}
