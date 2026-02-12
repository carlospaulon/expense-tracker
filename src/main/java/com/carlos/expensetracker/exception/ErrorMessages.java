package com.carlos.expensetracker.exception;

public final class ErrorMessages {
    private ErrorMessages() {}

    //400 - bad request
    public static final String VALIDATION_ERROR = "Validation failed";
    public static final String BAD_REQUEST = "Bad request";

    //401 - unauthorized
    public static final String UNAUTHORIZED = "Unauthorized Access";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String TOKEN_EXPIRED = "Token expired: Please login again";
    public static final String TOKEN_INVALID = "Invalid token: Authentication failed";
    public static final String TOKEN_MISSING = "Missing token: Please provide authentication token";

    //403 - forbidden
    public static final String FORBIDDEN = "Forbidden: You don't have permission to access this resource";

    //404 - not found
    public static final String RESOURCE_NOT_FOUND = "Resource not found";

    //409 - conflict
    public static final String CONFLICT = "Conflict: Resource already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already registered";
    public static final String USERNAME_ALREADY_EXISTS = "Username already registered";

    //429 - too many requests
    public static final String RATE_LIMIT_EXCEEDED = "Rate limit exceeded: Too many requests";

    //500 - server error
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    //503 - service unavailable
    public static final String DATABASE_UNAVAILABLE = "Database unavailable: Connection failed";

}
