package com.carlos.expensetracker.exception;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }

    public MissingTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
