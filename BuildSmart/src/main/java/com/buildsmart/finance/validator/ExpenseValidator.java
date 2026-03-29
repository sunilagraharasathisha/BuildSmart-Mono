package com.buildsmart.finance.validator;

import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.finance.dto.ExpenseRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ExpenseValidator {
    public void validate(ExpenseRequest request) {
        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("Expense description is required");
        }

        String normalized = request.description().replaceAll("[.,]", " ").trim();
        long wordCount = Arrays.stream(normalized.split("\\s+"))
                .filter(s -> !s.isBlank())
                .count();

        if (wordCount < 20) {
            throw new IllegalArgumentException("Expense description must be at least 20 words");
        }

        if (wordCount > 20) {
            // Allow but warn
            ApplicationLogger.log.warn("Description exceeds 20 words ({} words)", wordCount);
        }
    }
}
