package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.ExpenseStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequestDto {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Description is required")
    @Size(max = 500)
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String approvedBy;
    private ExpenseStatus status;
}
