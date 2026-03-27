package com.buildsmart.siteops.dto;

import com.buildsmart.siteops.enums.IssueStatus;
import jakarta.validation.constraints.Size;

/**
 * Partial update: only description, status, assignedTo, and resolutionNotes can change.
 * Severity cannot be changed after creation (audit trail).
 */
public record IssueUpdateRequest(

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        IssueStatus status,

        String assignedTo,

        @Size(max = 2000, message = "Resolution notes must not exceed 2000 characters")
        String resolutionNotes
) {}
