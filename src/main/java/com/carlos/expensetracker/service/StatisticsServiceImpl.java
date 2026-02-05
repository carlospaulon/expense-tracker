package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.response.CategorySummaryResponse;
import com.carlos.expensetracker.dto.response.ExpenseSummaryResponse;
import com.carlos.expensetracker.entity.enums.FilterPeriod;
import com.carlos.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService{
    private final ExpenseRepository expenseRepository;


    @Override
    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getSummary(UUID userId, ExpenseFilterRequest filter) {
        log.info("Calculating summary for user: {}", userId);

        //Simply with a method
        LocalDate startDate = filter != null && filter.getEffectiveStartDate() != null
                ? filter.getEffectiveStartDate()
                : FilterPeriod.LAST_MONTH.getStartDate();

        LocalDate endDate = filter != null && filter.getEffectiveEndDate() != null
                ? filter.getEffectiveEndDate()
                : LocalDate.now();

        return expenseRepository.calculateSummary(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getByCategory(UUID userId, ExpenseFilterRequest filter) {
        log.info("Calculating category breakdown for user: {}", userId);

        LocalDate startDate = filter != null && filter.getEffectiveStartDate() != null
                ? filter.getEffectiveStartDate()
                : FilterPeriod.LAST_MONTH.getStartDate();

        LocalDate endDate = filter != null && filter.getEffectiveEndDate() != null
                ? filter.getEffectiveEndDate()
                : LocalDate.now();

        List<CategorySummaryResponse> categories = expenseRepository.calculateByCategory(
                userId, startDate, endDate
        );


        BigDecimal total = categories.stream()
                .map(CategorySummaryResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);



        return categories.stream()
                .map(cat -> {
                    BigDecimal percentage = total.compareTo(BigDecimal.ZERO) > 0
                            ? cat.amount()
                            .divide(total, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;

                    return new CategorySummaryResponse(
                            cat.category(),
                            cat.amount(),
                            cat.count(),
                            percentage.setScale(2, RoundingMode.HALF_UP)
                    );
                })
                .toList();
    }
}
