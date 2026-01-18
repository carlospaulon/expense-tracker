package com.carlos.expensetracker.exception;

public class BadRequestException extends RuntimeException {
    //400
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
