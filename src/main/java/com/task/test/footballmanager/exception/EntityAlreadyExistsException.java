package com.task.test.footballmanager.exception;

import javax.persistence.PersistenceException;

public class EntityAlreadyExistsException extends PersistenceException {
    private static final String ENTITY_ALREADY_EXISTS =
        "Such entity already exists";

    public EntityAlreadyExistsException(String message) {
        super(message.isEmpty() ? ENTITY_ALREADY_EXISTS : message);
    }

    public EntityAlreadyExistsException() {
        super(ENTITY_ALREADY_EXISTS);
    }
}
