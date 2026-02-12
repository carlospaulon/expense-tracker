package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import com.carlos.expensetracker.entity.Expense;
import com.carlos.expensetracker.entity.User;
import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import com.carlos.expensetracker.exception.DatabaseException;
import com.carlos.expensetracker.exception.ResourceNotFoundException;
import com.carlos.expensetracker.mapper.ExpenseMapper;
import com.carlos.expensetracker.repository.ExpenseRepository;
import com.carlos.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public ExpenseResponse createExpense(UUID userId, CreateExpenseRequest request) {
        log.info("Creating expense for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Expense expense = expenseMapper.toEntity(request, user);

        try {
            Expense savedExpense = expenseRepository.save(expense);
            log.debug("Expense created: {} (amount: {})", savedExpense.getId(), savedExpense.getAmount());

            return expenseMapper.toResponse(savedExpense);

        } catch (DataAccessException ex) {
            log.warn("Database error while creating expense for user: {}", userId, ex);
            throw new DatabaseException("Failed to save expense", ex);
        }


    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses(UUID userId) {
        log.info("Fetching all expenses for user: {}", userId);

        return expenseRepository.findByUserId(userId)
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpenses(UUID userId, Pageable pageable) {
        log.info("Fetching all expenses for user: {} (page: {})", userId, pageable.getPageNumber());

        return expenseRepository.findByUserId(userId, pageable)
                .map(expenseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(UUID userId, UUID expenseId) {
        log.info("Fetching expense: {} for user: {}", expenseId, userId);

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        return expenseMapper.toResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(UUID userId, UUID expenseId, UpdateExpenseRequest request) {
        log.info("Updating expense: {} for user: {}", expenseId, userId);

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        expenseMapper.updateEntity(expense, request);

        Expense updatedExpense = expenseRepository.save(expense);
        log.info("Expense updated: {}", updatedExpense.getId());

        return expenseMapper.toResponse(updatedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(UUID userId, UUID expenseId) {
        log.info("Deleting expense: {} for user: {}", expenseId, userId);

        if (!expenseRepository.existsByIdAndUserId(expenseId, userId)) {
            throw new ResourceNotFoundException("Expense not found");
        }

        expenseRepository.deleteByIdAndUserId(expenseId, userId);
        log.info("Expense deleted: {}", expenseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesWithFilter(UUID userId, ExpenseFilterRequest filter, Pageable pageable) {
        log.info("Fetching expenses for user: {} with filters: {}", userId, filter);

        if (filter == null || !filter.hasFilters()) {
            return expenseRepository.findByUserId(userId, pageable).map(expenseMapper::toResponse);
        }

        Page<Expense> expenses;

        LocalDate startDate = filter.getEffectiveStartDate();
        LocalDate endDate = filter.getEffectiveEndDate();
        ExpenseCategory category = filter.category();

        if (category != null && startDate != null && endDate != null) {
            expenses = expenseRepository.findByUserIdAndCategoryAndExpenseDateBetween(
                    userId,
                    category,
                    startDate,
                    endDate,
                    pageable
            );
        } else if (category != null) {
            expenses = expenseRepository.findByUserIdAndCategory(
                    userId,
                    category,
                    pageable
            );
        } else if (startDate != null && endDate != null) {
            expenses = expenseRepository.findByUserIdAndExpenseDateBetween(
                    userId,
                    startDate,
                    endDate,
                    pageable
            );
        } else {
            //fallback
            expenses = expenseRepository.findByUserId(userId, pageable);
        }


        return expenses.map(expenseMapper::toResponse);
    }
}
