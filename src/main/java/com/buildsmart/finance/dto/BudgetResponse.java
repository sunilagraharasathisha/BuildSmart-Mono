package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.BudgetCategory;

import java.math.BigDecimal;

public record BudgetResponse(
        String budgetId,
        String projectId,
        BudgetCategory category,
        BigDecimal plannedAmount,
        BigDecimal actualAmount,
        BigDecimal variance
) {
}
