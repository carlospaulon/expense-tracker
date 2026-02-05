package com.carlos.expensetracker.controller;

import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.response.CategorySummaryResponse;
import com.carlos.expensetracker.dto.response.ExpenseSummaryResponse;
import com.carlos.expensetracker.security.CustomUserDetails;
import com.carlos.expensetracker.service.StatisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryResponse> getSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid ExpenseFilterRequest filter
    ) {
        UUID userId = userDetails.getUserId();
        log.info("GET /api/statistics/summary - user: {}", userId);

        ExpenseSummaryResponse summary = statisticsService.getSummary(userId, filter);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CategorySummaryResponse>> getByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid ExpenseFilterRequest filter
    ) {

        UUID userId = userDetails.getUserId();
        log.info("GET /api/statistics/by-category - user: {}", userId);

        List<CategorySummaryResponse> categories = statisticsService.getByCategory(userId, filter);

        return ResponseEntity.ok(categories);

    }
}
