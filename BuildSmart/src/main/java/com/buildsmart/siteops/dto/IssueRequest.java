package com.buildsmart.siteops.dto;

import com.buildsmart.siteops.enums.IssueSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IssueRequest(

        @NotBlank(message = "Project ID is required")
        String projectId,

        /** Optional: links this issue to the SiteLog where it was first observed. */
        String logId,

        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Severity is required (LOW, MEDIUM, HIGH, CRITICAL)")
        IssueSeverity severity,

        @NotBlank(message = "reportedBy (user ID) is required")
        String reportedBy,

        /** Optional: pre-assign to a user at creation time. */
        String assignedTo
) {}
