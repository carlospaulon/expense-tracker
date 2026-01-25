package com.carlos.expensetracker.dto.response;

import java.math.BigDecimal;

public record ExpenseSummaryResponse(
        BigDecimal total,
        Long count,
        BigDecimal average,
        BigDecimal min,
        BigDecimal max
) {
}
