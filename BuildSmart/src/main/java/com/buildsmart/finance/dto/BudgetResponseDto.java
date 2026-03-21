package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.BudgetCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetResponseDto {

    private String budgetId;
    private String projectId;
    private String projectName;
    private BudgetCategory category;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;
    private BigDecimal variance;
}
