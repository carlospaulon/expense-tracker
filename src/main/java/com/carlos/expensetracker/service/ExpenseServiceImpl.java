package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import com.carlos.expensetracker.entity.Expense;
import com.carlos.expensetracker.entity.User;
import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import com.carlos.expensetracker.exception.ResourceNotFoundException;
import com.carlos.expensetracker.mapper.ExpenseMapper;
import com.carlos.expensetracker.repository.ExpenseRepository;
import com.carlos.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ExpenseServiceImpl implements ExpenseService{
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

        Expense savedExpense = expenseRepository.save(expense);
        log.debug("Expense created: {} (amount: {})", savedExpense.getId(), savedExpense.getAmount());

        return expenseMapper.toResponse(savedExpense);
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
    public List<ExpenseResponse> getExpensesByCategory(UUID userId, ExpenseCategory category) {
        log.info("Fetching expenses for user: {} and category: {}", userId, category);

        return expenseRepository.findByUserIdAndCategory(userId, category)
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByPeriod(UUID userId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching expenses for user: {} between {} and {}", userId, startDate, endDate);

        return expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate)
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Override
    public List<ExpenseResponse> getExpensesByCategoryAndPeriod(UUID userId, ExpenseCategory category, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching expenses for user: {}, category: {}, period: {} to {}",
                userId, category, startDate, endDate);

        return expenseRepository.findByUserIdAndCategoryAndExpenseDateBetween(
                userId, category, startDate, endDate
        )
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }
}
