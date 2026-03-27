package com.buildsmart.vendor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DeliveryRequest(
        @NotBlank String contractId,
        @NotNull LocalDate date,
        @NotBlank String item,
        @Min(1) int quantity
) {
}