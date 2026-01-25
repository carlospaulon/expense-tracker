package com.carlos.expensetracker.mapper;

import com.carlos.expensetracker.dto.request.CreateExpenseRequest;
import com.carlos.expensetracker.dto.request.UpdateExpenseRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import com.carlos.expensetracker.entity.Expense;
import com.carlos.expensetracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    //request to entity
    public Expense toEntity(CreateExpenseRequest request, User user) {
        return Expense.builder()
                .user(user)
                .amount(request.amount())
                .category(request.category())
                .description(request.description())
                .expenseDate(request.expenseDate())
                .build();
    }

    //update - avoiding nullPointer
    public void updateEntity(Expense expense, UpdateExpenseRequest request) {
        if (request.amount() != null) {
            expense.setAmount(request.amount());
        }
        if (request.category() != null) {
            expense.setCategory(request.category());
        }
        if (request.description() != null) {
            expense.setDescription(request.description());
        }
        if (request.expenseDate() != null) {
            expense.setExpenseDate(request.expenseDate());
        }
    }

    //entity to response
    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getUser().getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
