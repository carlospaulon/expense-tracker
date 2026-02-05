package com.carlos.expensetracker.dto.response;

import com.carlos.expensetracker.entity.enums.ExpenseCategory;

import java.math.BigDecimal;

public record CategorySummaryResponse(
        ExpenseCategory category,
        BigDecimal amount,
        Long count,
        BigDecimal percentage
) {
}
