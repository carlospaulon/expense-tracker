package com.carlos.expensetracker.dto.response;

import com.carlos.expensetracker.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID userId,
        BigDecimal amount,
        ExpenseCategory category,
        String description,
        LocalDate expenseDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
