package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.response.CategorySummaryResponse;
import com.carlos.expensetracker.dto.response.ExpenseSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface StatisticsService {

    ExpenseSummaryResponse getSummary(UUID userId, ExpenseFilterRequest filter);

    List<CategorySummaryResponse> getByCategory(UUID userId, ExpenseFilterRequest filter);
}
