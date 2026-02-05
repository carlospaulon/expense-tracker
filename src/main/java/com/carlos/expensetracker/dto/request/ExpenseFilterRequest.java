package com.carlos.expensetracker.dto.request;

import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import com.carlos.expensetracker.entity.enums.FilterPeriod;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ExpenseFilterRequest(
        ExpenseCategory category,

        FilterPeriod period,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {

    @AssertTrue(message = "Start date must be before or equal to end date")
    public boolean isDateRangeValid() {
        if (startDate != null && endDate != null) {
            return !startDate.isAfter(endDate);
        }

        return true;
    }

    public LocalDate getEffectiveStartDate() {
        if (period != null && period != FilterPeriod.CUSTOM) {
            return period.getStartDate();
        }

        return startDate;
    }

    public LocalDate getEffectiveEndDate() {
        if (period != null && period != FilterPeriod.CUSTOM) {
            return period.getEndDate();
        }

        return endDate;
    }

    public boolean hasFilters() {
        //efetivo, mas deixar mais clean
        return category != null || period != null || startDate != null || endDate != null;
    }

}
