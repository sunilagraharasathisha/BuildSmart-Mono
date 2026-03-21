package com.buildsmart.common.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String identifier) {
        super(String.format("%s already exists with identifier: %s", resource, identifier));
    }
}
