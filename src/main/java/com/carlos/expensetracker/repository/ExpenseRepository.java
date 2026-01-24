package com.carlos.expensetracker.repository;

import com.carlos.expensetracker.entity.Expense;
import com.carlos.expensetracker.entity.enums.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByUserId(UUID userId);

    Page<Expense> findByUserId(
            UUID userId,
            Pageable pageable
    );

    List<Expense> findByUserIdAndExpenseDateBetween(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense> findByUserIdAndCategory(
            UUID userId,
            ExpenseCategory category
    );

    List<Expense> findByUserIdAndCategoryAndExpenseDateBetween(
            UUID userId,
            ExpenseCategory category,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Expense> findByIdAndUserId(
            UUID id,
            UUID userID
    );

    void deleteByIdAndUserId(
            UUID expenseId,
            UUID userId
    );

    boolean existsByIdAndUserId(
            UUID id,
            UUID userId
    );

    //Custom queries
    @Query("SELECT COALESCE(SUM(e.amount), 0.00) FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalByUserAndPeriod(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0.00) FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.category = :category " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalByCategoryAndPeriod(
            @Param("userId") UUID userId,
            @Param("category") ExpenseCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
