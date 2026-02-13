package com.carlos.expensetracker.dto.request;

import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import com.carlos.expensetracker.entity.enums.FilterPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ExpenseFilterRequest(
        @Schema(description = "Expense category", example = "GROCERIES")
        ExpenseCategory category,

        @Schema(description = "Expense period", example = "LAST_MONTH")
        FilterPeriod period,

        @Schema(description = "Start date (ISO format)", example = "2026-02-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @Schema(description = "End date (ISO format)", example = "2026-02-28")
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
