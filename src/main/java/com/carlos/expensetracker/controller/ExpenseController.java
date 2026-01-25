package com.carlos.expensetracker.controller;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import com.carlos.expensetracker.security.CustomUserDetails;
import com.carlos.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateExpenseRequest request
    ) {
        UUID userId = userDetails.getUserId();
        log.info("POST /api/expenses - user: {}", userId);

        ExpenseResponse response = expenseService.createExpense(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "expenseDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses - user: {} (page: {})", userId, pageable.getPageNumber());

        Page<ExpenseResponse> expenses = expenseService.getAllExpenses(userId, pageable);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/{} - user: {}", id, userId);

        ExpenseResponse response = expenseService.getExpenseById(userId, id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest request
    ) {
        UUID userId = userDetails.getUserId();
        log.info("PUT /api/expenses/{} - user: {}", id, userId);

        ExpenseResponse expense = expenseService.updateExpense(userId, id, request);

        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID userId = userDetails.getUserId();
        log.info("DELETE /api/expenses/{} - user: {}", id, userId);

        expenseService.deleteExpense(userId, id);

        return ResponseEntity.noContent().build();
    }

    //Filters
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable ExpenseCategory category
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/category/{} - user: {}", category, userId);

        List<ExpenseResponse> expenses = expenseService.getExpensesByCategory(userId, category);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/period")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByPeriod(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/period?startDate={}&endDate={} - user: {}", startDate, endDate, userId);

        List<ExpenseResponse> expenses = expenseService.getExpensesByPeriod(userId, startDate, endDate);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseResponse>> getExpensesWithFilter(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/filter - user: {}, category: {}, period: {} to {}",
                userId, category, startDate, endDate);

        List<ExpenseResponse> expenses;

        if (category != null && startDate != null && endDate != null) {
            expenses = expenseService.getExpensesByCategoryAndPeriod(
                    userId, category, startDate, endDate
            );
        } else if (category != null) {
            expenses = expenseService.getExpensesByCategory(userId, category);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.getExpensesByPeriod(userId, startDate, endDate);
        } else {
            expenses = expenseService.getAllExpenses(userId);
        }

        return ResponseEntity.ok(expenses);
    }


}
