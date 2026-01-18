package com.carlos.expensetracker.exception;

import com.carlos.expensetracker.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                ex.getMessage(),
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
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    //404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request
    ) {

        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request
    ) {
        log.error("Internal server error: {}", ex.getMessage(), ex);

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request //tem alguma maneira de deixar sem estes nulls para as exceptions que não tem validationErrors?
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    //ValidationError
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

        log.error("Validation failed: {}", validationErrors);

        ErrorResponse error = buildErrorMessage(
                ex.getMessage(), //It is not this type of message, i create a variable and give the message
                HttpStatus.BAD_REQUEST,
                request,
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }


    //Utility - Override methods its a good thing to do???? its not repetitive code?
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


