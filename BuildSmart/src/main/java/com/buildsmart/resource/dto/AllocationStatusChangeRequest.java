package com.buildsmart.resource.dto;

import jakarta.validation.constraints.NotBlank;

public record AllocationStatusChangeRequest(
        @NotBlank(message = "Status is required (Planned, Active, Released, Cancelled)")
        String status
) {}
