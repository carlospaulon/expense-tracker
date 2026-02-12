package com.carlos.expensetracker.exception;

import com.carlos.expensetracker.dto.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request
    ) {

        log.error("Bad request: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.BAD_REQUEST,
                HttpStatus.BAD_REQUEST,
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            validationErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        });

        log.warn("Validation failed: {}", validationErrors);

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST,
                request,
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        String message = "Malformed JSON request";
        if (ex.getMessage().contains("JSON parse error")) {
            message = "Invalid JSON format: Check your request body";
        }

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.BAD_REQUEST,
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request
    ) {

        log.error("Unauthorized: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED,
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(
            ExpiredJwtException ex, HttpServletRequest request
    ) {
        log.warn("JWT token expired: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.TOKEN_EXPIRED,
                HttpStatus.UNAUTHORIZED,
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<ErrorResponse> handleInvalidJwtException(
            Exception ex, HttpServletRequest request
    ) {
        log.warn("Invalid JWT token: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.TOKEN_INVALID,
                HttpStatus.UNAUTHORIZED,
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    //is it right? it's necessary to create a new exception for a missing token?
    @ExceptionHandler(MissingTokenException.class)
    public ResponseEntity<ErrorResponse> handleMissingJwtException(
            MissingTokenException ex, HttpServletRequest request
    ) {
        log.warn("Missing JWT token: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.TOKEN_MISSING,
                HttpStatus.UNAUTHORIZED,
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    //403
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex, HttpServletRequest request
    ) {
        log.warn("Forbidden: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                request
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request
    ) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.FORBIDDEN,
                HttpStatus.FORBIDDEN,
                request
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    //404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request
    ) {

        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.RESOURCE_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex, HttpServletRequest request
    ) {
        log.warn("Conflict: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());

        String message = ErrorMessages.CONFLICT;

        // Check for specific constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("users_username_key")) {
                message = ErrorMessages.USERNAME_ALREADY_EXISTS;
            } else if (ex.getMessage().contains("users_email_key")) {
                message = ErrorMessages.EMAIL_ALREADY_EXISTS;
            }
        }

        ErrorResponse error = buildErrorMessage(
                message,
                HttpStatus.CONFLICT,
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    //429
    @ExceptionHandler(RateLimitingException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitingException ex, HttpServletRequest request
    ) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.RATE_LIMIT_EXCEEDED,
                HttpStatus.TOO_MANY_REQUESTS,
                request
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(error);
    }



    //503
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex, HttpServletRequest request) {
        log.error("Database error: {}", ex.getMessage(), ex);

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.DATABASE_UNAVAILABLE,
                HttpStatus.SERVICE_UNAVAILABLE,
                request
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    //500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request
    ) {
        log.error("Internal server error: {}", ex.getMessage(), ex);

        ErrorResponse error = buildErrorMessage(
                ErrorMessages.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    //Utility - Override methods it's a good thing to do???? its not repetitive code?
    //Ou utilizar validationErrors != null ? validationErrors : Map.of() - passando null no builder
    private ErrorResponse buildErrorMessage(
            String message,
            HttpStatus status,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {

        return new ErrorResponse(
                message,
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                request != null ? request.getRequestURI() : null,
                validationErrors != null ? validationErrors : Map.of()
        );
    }

    private ErrorResponse buildErrorMessage(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {

        return new ErrorResponse(
                message,
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                request != null ? request.getRequestURI() : null,
                Map.of()
        );
    }

}


