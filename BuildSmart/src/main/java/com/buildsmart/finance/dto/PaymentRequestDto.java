package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequestDto {

    @NotBlank(message = "Invoice ID is required")
    private String invoiceId;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount must not be negative")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private PaymentStatus status;
}
