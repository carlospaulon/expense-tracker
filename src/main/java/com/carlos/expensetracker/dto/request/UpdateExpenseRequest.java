package com.carlos.expensetracker.dto.request;

import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateExpenseRequest(
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @DecimalMax(value = "99999999.99", message = "Amount to large")
        @Digits(integer = 8, fraction = 2, message = "Amount must have at most 2 decimal places")
        BigDecimal amount,

        ExpenseCategory category,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @PastOrPresent(message = "Expense date cannot be in the future")
        LocalDate expenseDate
) {
}
