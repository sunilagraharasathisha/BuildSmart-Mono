package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.ExpenseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank String projectId,
        @NotBlank String description,
        @NotNull LocalDate date,
        @NotBlank String approvedBy,
        @NotNull ExpenseStatus status
) {
}
