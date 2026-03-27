package com.buildsmart.vendor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record InvoiceResponse(
        String invoiceId,
        String contractId,
        BigDecimal amount,
        LocalDate date,
        String status,
        boolean paid,
        boolean overdue
) {
    public static InvoiceResponse of(
            String invoiceId,
            String contractId,
            BigDecimal amount,
            LocalDate date,
            String status
    ) {
        boolean paid =
                "PAID".equalsIgnoreCase(status);

        long ageInDays =
                ChronoUnit.DAYS.between(date, LocalDate.now());

        boolean overdue =
                !paid && ageInDays > 30;

        return new InvoiceResponse(
                invoiceId,
                contractId,
                amount,
                date,
                status,
                paid,
                overdue
        );
    }
}