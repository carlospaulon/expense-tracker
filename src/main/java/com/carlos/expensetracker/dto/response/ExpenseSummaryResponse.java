package com.carlos.expensetracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseSummaryResponse(
        BigDecimal totalAmount,
        Long totalCount,
        Double averageAmount,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        LocalDate  periodStart,
        LocalDate periodEnd
) {
}
