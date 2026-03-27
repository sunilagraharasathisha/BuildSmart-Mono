package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.BudgetCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BudgetRequest(
        @NotBlank String projectId,
        @NotNull BudgetCategory category,
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal plannedAmount,
        @DecimalMin(value = "0.00", inclusive = true) BigDecimal actualAmount
) {}
