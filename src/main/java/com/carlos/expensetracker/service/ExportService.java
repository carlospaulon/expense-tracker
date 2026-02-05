package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;

import java.util.UUID;

public interface ExportService {

    //Expense csv
    byte[] exportToCsv(UUID userId, ExpenseFilterRequest filter);
}
