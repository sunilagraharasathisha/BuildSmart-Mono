package com.buildsmart.finance.validator;

import com.buildsmart.finance.dto.ExpenseRequest;
import org.springframework.stereotype.Component;

@Component
public class ExpenseValidator {
    public void validate(ExpenseRequest request) {
        if (request.description().isBlank()) {
            throw new IllegalArgumentException("Expense description is required");
        }
    }
}
