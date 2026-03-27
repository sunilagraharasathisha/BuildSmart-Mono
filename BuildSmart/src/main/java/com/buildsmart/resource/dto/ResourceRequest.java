package com.buildsmart.resource.dto;

import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ResourceRequest(

        @NotBlank(message = "Resource name is required")
        @Size(max = 120, message = "Resource name must not exceed 120 characters")
        String resourceName,

        @NotNull(message = "Resource type is required (Labor or Equipment)")
        ResourceType type,

        @NotNull(message = "Availability status is required")
        ResourceAvailability availability,

        @NotNull(message = "Cost rate is required")
        @DecimalMin(value = "0.01", message = "Cost rate must be greater than zero")
        BigDecimal costRate,

        @Size(max = 300, message = "Description must not exceed 300 characters")
        String description
) {}
