package com.buildsmart.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExpenseRequest(
                @NotBlank String projectId,
                @NotBlank String description,
                @NotNull LocalDate date) {
}
