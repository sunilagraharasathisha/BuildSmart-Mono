package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.ExpenseStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseResponseDto {

    private String expenseId;
    private String projectId;
    private String projectName;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String approvedBy;
    private ExpenseStatus status;
}
