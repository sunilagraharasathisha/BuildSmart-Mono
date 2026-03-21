package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.BudgetCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequestDto {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotNull(message = "Category is required")
    private BudgetCategory category;

    @NotNull(message = "Planned amount is required")
    @PositiveOrZero(message = "Planned amount must not be negative")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal plannedAmount;

    @PositiveOrZero(message = "Actual amount must not be negative")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal actualAmount;
}
