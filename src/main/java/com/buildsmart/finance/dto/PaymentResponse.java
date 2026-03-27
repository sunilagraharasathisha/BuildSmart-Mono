package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
        String paymentId,
        String invoiceId,
        BigDecimal amount,
        LocalDate date,
        PaymentStatus status
) {
}
