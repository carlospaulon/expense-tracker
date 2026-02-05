package com.carlos.expensetracker.controller;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import com.carlos.expensetracker.security.CustomUserDetails;
import com.carlos.expensetracker.service.ExpenseService;
import com.carlos.expensetracker.service.ExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExportService exportService;

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

    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseResponse>> searchExpenses(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid ExpenseFilterRequest filter,
            @PageableDefault(size = 10, sort = "expenseDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/search - user: {}, filter: {}", userId, filter);

        Page<ExpenseResponse> expenses = expenseService.getExpensesWithFilter(userId, filter, pageable);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportToCsv(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid ExpenseFilterRequest filter
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/export/csv - user: {}", userId);

        byte[] csv = exportService.exportToCsv(userId, filter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "expenses.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }


}
