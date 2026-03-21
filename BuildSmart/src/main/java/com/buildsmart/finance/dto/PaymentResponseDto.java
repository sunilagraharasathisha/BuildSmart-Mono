package com.buildsmart.finance.dto;

import com.buildsmart.common.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PaymentResponseDto {

    private String paymentId;
    private String invoiceId;
    private String invoiceNumber;
    private BigDecimal amount;
    private LocalDate date;
    private PaymentStatus status;
}
