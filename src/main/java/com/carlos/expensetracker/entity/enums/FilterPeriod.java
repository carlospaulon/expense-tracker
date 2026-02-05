package com.carlos.expensetracker.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public enum FilterPeriod {

    LAST_WEEK("Last 7 days", 7),
    LAST_MONTH("Last 30 days", 30),
    LAST_3_MONTHS("Last 90 days", 90),
    CUSTOM("Custom period", 0);

    private final String description;
    private final int days;

    public LocalDate getStartDate() {
        if (this == CUSTOM) {
            throw new IllegalStateException("Custom period requires explicit dates");
        }

        return LocalDate.now().minusDays(days);
    }

    public LocalDate getEndDate() {
        return LocalDate.now();
    }
}
