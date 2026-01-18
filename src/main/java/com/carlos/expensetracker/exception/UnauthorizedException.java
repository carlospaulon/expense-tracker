package com.carlos.expensetracker.exception;

public class UnauthorizedException extends RuntimeException {
    //401
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
