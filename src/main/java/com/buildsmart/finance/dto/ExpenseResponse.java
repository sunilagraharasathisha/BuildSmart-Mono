package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.ExpenseStatus;

import java.time.LocalDate;

public record ExpenseResponse(
        String expenseId,
        String projectId,
        String description,
        LocalDate date,
        String approvedBy,
        ExpenseStatus status
) {
}
