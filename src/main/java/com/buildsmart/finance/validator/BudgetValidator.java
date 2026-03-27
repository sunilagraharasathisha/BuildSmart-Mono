package com.buildsmart.finance.validator;

import com.buildsmart.finance.dto.BudgetRequest;
import org.springframework.stereotype.Component;

@Component
public class BudgetValidator {
    public void validate(BudgetRequest request) {
        if (request.plannedAmount().signum() < 0 || request.actualAmount().signum() < 0) {
            throw new IllegalArgumentException("Budget amounts cannot be negative");
        }
    }
}
