package com.buildsmart.vendor.dto;

import java.time.LocalDate;

public record DeliveryResponse(
        String deliveryId,
        String contractId,
        LocalDate date,
        String item,
        int quantity,
        String status,
        boolean completed,
        boolean delayed
) {
    public static DeliveryResponse of(
            String deliveryId,
            String contractId,
            LocalDate date,
            String item,
            int quantity,
            String status
    ) {
        boolean completed =
                "COMPLETED".equalsIgnoreCase(status);

        boolean delayed =
                date.isAfter(LocalDate.now());

        return new DeliveryResponse(
                deliveryId,
                contractId,
                date,
                item,
                quantity,
                status,
                completed,
                delayed
        );
    }
}
