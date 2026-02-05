package com.carlos.expensetracker.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyTrendResponse(
        YearMonth month,
        BigDecimal totalAmount,
        Long count
) {
}
