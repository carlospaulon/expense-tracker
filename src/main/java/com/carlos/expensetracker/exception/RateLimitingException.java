package com.carlos.expensetracker.exception;

public class RateLimitingException extends RuntimeException {
    private final long retryAfterSeconds;

    public RateLimitingException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
