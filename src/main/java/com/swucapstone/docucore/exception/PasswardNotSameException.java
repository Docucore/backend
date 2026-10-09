package com.swucapstone.docucore.exception;

public class PasswardNotSameException extends RuntimeException {
    public PasswardNotSameException(String message) {
        super(message);
    }
    public PasswardNotSameException() {
        super("Passward not same");
    }
}
