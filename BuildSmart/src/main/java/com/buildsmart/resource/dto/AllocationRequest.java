package com.buildsmart.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Raised by a Site Engineer when creating a daily site log and needing resources.
 * The Project Manager sees this as a Planned allocation and activates it.
 */
public record AllocationRequest(

        @NotBlank(message = "Project ID is required")
        String projectId,

        @NotBlank(message = "Resource ID is required")
        String resourceId,

        @NotNull(message = "Assigned (start) date is required")
        LocalDate assignedDate,

        /**
         * Nullable: open-ended allocation until the resource is explicitly released.
         */
        LocalDate releasedDate,

        @NotBlank(message = "Status is required (Planned, Active, Released, Cancelled)")
        String status,

        @Size(max = 500, message = "Request notes must not exceed 500 characters")
        String requestNotes
) {}
