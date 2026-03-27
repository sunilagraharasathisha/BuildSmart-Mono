package com.buildsmart.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VendorRequest(
        @NotBlank String name,
        @NotBlank String contactInfo,
        @NotNull String status
) {
}