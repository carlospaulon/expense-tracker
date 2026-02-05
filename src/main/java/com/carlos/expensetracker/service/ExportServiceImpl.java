package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.ExpenseFilterRequest;
import com.carlos.expensetracker.dto.response.ExpenseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExpenseService expenseService;

    @Override
    public byte[] exportToCsv(UUID userId, ExpenseFilterRequest filter) {
        log.info("Exporting expenses to CSV for user: {}", userId);

        //search for all expenses
        List<ExpenseResponse> expenses = expenseService.getExpensesWithFilter(
                userId,
                filter,
                Pageable.unpaged()
        ).getContent();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos, false, StandardCharsets.UTF_8)) {

            writer.println("ID,Date,Category,Amount,Description");

            for (ExpenseResponse expense : expenses) {
                writer.printf("%s,%s,%s,%.2f,\"%s\"%n",
                        expense.id(),
                        expense.expenseDate(),
                        expense.category(),
                        expense.amount(),
                        expense.description() != null ? expense.description().replace("\"", "\"\"") : ""

                );
            }

            writer.flush(); //review this
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting CSV", e);
            throw new RuntimeException("Failed to export CSV", e);
        }

    }
}
