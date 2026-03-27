package com.buildsmart.vendor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ContractResponse(
        String contractId,
        String vendorId,
        String projectId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal value,
        String status,
        long durationInDays,
        boolean expired,
        boolean active
) {
    public static ContractResponse of(
            String contractId,
            String vendorId,
            String projectId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal value,
            String status
    ) {
        long duration =
                ChronoUnit.DAYS.between(startDate, endDate);

        boolean expired =
                endDate.isBefore(LocalDate.now());

        boolean active =
                "ACTIVE".equalsIgnoreCase(status);

        return new ContractResponse(
                contractId,
                vendorId,
                projectId,
                startDate,
                endDate,
                value,
                status,
                duration,
                expired,
                active
        );
    }
}