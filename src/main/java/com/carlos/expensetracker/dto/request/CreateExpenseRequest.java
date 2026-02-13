package com.carlos.expensetracker.dto.request;

import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @DecimalMax(value = "99999999.99", message = "Amount to large")
        @Digits(integer = 8, fraction = 2, message = "Amount must have at most 2 decimal places")
        @Schema(example = "10.00", description = "Expense amount")
        BigDecimal amount,

        @NotNull(message = "Category is required")
        @Schema(example = "GROCERIES", description = "Expense category")
        ExpenseCategory category,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        @Schema(example = "Week expenses", description = "Expense description")
        String description,

        @NotNull(message = "Expense date is required")
        @PastOrPresent(message = "Expense date cannot be in the future")
        @Schema(example = "2026-02-13", description = "Expense date")
        LocalDate expenseDate
) {
}
