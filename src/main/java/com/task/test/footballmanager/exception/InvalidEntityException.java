package com.task.test.footballmanager.exception;

import javax.persistence.PersistenceException;

public class InvalidEntityException extends PersistenceException {
    private static final String INVALID_ENTITY = "Entity is not valid";

    public InvalidEntityException(String message) {
        super(message.isEmpty() ? INVALID_ENTITY : message);
    }

    public InvalidEntityException() {
        super(INVALID_ENTITY);
    }
}
