package com.carlos.expensetracker.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp,
        int status,
        String error,
        String path,

        Map<String, String> validationErrors
) {
}
