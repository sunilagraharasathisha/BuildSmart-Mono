package com.buildsmart.siteops.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SiteLogRequest(

        @NotBlank(message = "Project ID is required")
        String projectId,

        @NotNull(message = "Log date is required")
        LocalDate logDate,

        @Size(max = 2000, message = "Activities must not exceed 2000 characters")
        String activities,

        @Size(max = 2000, message = "Issues summary must not exceed 2000 characters")
        String issuesSummary,

        @NotNull(message = "Progress percent is required")
        @DecimalMin(value = "0.00", message = "Progress percent must be >= 0")
        @DecimalMax(value = "100.00", message = "Progress percent must be <= 100")
        BigDecimal progressPercent,

        @NotBlank(message = "submittedBy (user ID) is required")
        String submittedBy
) {}
