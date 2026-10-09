package com.swucapstone.docucore.exception;

public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException() {
        super("Username already exists");
    }
}
