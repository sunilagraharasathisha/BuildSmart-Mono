package com.buildsmart.finance.validator;

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

        if (wordCount == 0) {
            throw new IllegalArgumentException("Expense description is required");
        }

        if (wordCount > 20) {
            throw new IllegalArgumentException("Description exceeds allowed word limit.");
        }
    }
}
