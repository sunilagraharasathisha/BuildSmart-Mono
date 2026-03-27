package com.buildsmart.siteops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload used by a Project Manager to REJECT a resource request.
 *
 * Example:
 * {
 *   "actionedBy": "USR00001",
 *   "rejectionReason": "No equipment available in the requested date range. Please request after 2026-04-20."
 * }
 */
public record ResourceRequestRejectionRequest(

        @NotBlank(message = "actionedBy (PM user ID) is required")
        String actionedBy,

        @NotBlank(message = "Rejection reason is required so the Site Engineer understands the decision")
        @Size(max = 1000, message = "Rejection reason must not exceed 1000 characters")
        String rejectionReason
) {}
