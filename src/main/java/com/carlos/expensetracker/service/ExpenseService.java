package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponse createExpense(UUID userId, CreateExpenseRequest request);

    List<ExpenseResponse> getAllExpenses(UUID userId);

    Page<ExpenseResponse> getAllExpenses(UUID userId, Pageable pageable);

    ExpenseResponse getExpenseById(UUID userId, UUID expenseId);

    ExpenseResponse updateExpense(UUID userId, UUID expenseId, UpdateExpenseRequest request);

    void deleteExpense(UUID userId, UUID expenseId);

    Page<ExpenseResponse> getExpensesWithFilter(
            UUID userId,
            ExpenseFilterRequest filter,
            Pageable pageable
    );
}
