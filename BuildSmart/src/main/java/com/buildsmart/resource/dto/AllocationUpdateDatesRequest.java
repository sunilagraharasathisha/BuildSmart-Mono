package com.buildsmart.resource.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AllocationUpdateDatesRequest(
        @NotNull(message = "assignedDate is required")
        LocalDate assignedDate,
        LocalDate releasedDate
) {}
