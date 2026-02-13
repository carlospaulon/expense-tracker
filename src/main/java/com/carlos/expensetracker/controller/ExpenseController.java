package com.carlos.expensetracker.controller;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import com.carlos.expensetracker.security.CustomUserDetails;
import com.carlos.expensetracker.service.ExpenseService;
import com.carlos.expensetracker.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "Create expense")
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

    //todo: don't show error message with token issues
    @Operation(
            summary = "Get all expenses",
            description = "Returns a paginated list of expenses for a authenticated user"
    )
    @Parameters({
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of expenses per page", example = "10"),
            @Parameter(name = "sort", description = "Sorting page (e.g. expenseDate,desc)", example = "expenseDate,desc"),
    })
    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "expenseDate", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses - user: {} (page: {})", userId, pageable.getPageNumber());

        Page<ExpenseResponse> expenses = expenseService.getAllExpenses(userId, pageable);

        return ResponseEntity.ok(expenses);
    }

    @Operation(summary = "Get expense by id")
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

    @Operation(summary = "Update expense by id")
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

    @Operation(summary = "Delete expense by id")
    @ApiResponses(
            @ApiResponse(responseCode = "204", description = "Excluded expense")
    )
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

    @Operation(summary = "Search expenses")
    @Parameters({
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of expenses per page", example = "10"),
            @Parameter(name = "sort", description = "Sorting page (e.g. expenseDate,desc)", example = "expenseDate,desc"),
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseResponse>> searchExpenses(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @ParameterObject
            @Parameter(required = false, description = "Optional filter for export")
            @Valid
            ExpenseFilterRequest filter,

            @PageableDefault(size = 10, sort = "expenseDate", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/expenses/search - user: {}, filter: {}", userId, filter);

        Page<ExpenseResponse> expenses = expenseService.getExpensesWithFilter(userId, filter, pageable);

        return ResponseEntity.ok(expenses);
    }

    @Operation(summary = "Export as a csv file the expenses by user")
    @ApiResponse(
            responseCode = "200",
            description = "CSV file generated",
            content = @Content(mediaType = "text/csv")
    )
    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportToCsv(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @ParameterObject
            @Parameter(required = false, description = "Optional filter for export")
            @Valid
            ExpenseFilterRequest filter
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
