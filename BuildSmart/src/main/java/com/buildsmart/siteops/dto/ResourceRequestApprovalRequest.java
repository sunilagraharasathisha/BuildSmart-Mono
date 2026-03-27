package com.buildsmart.siteops.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload used by a Project Manager to APPROVE a resource request.
 *
 * On approval the system:
 *  1. Sets status = APPROVED on this request.
 *  2. Auto-creates an Active Allocation in the Resource module.
 *  3. Notifies the Site Engineer.
 *
 * Example:
 * {
 *   "actionedBy": "USR00001",
 *   "resourceId": "RESBS003"
 * }
 */
public record ResourceRequestApprovalRequest(

        @NotBlank(message = "actionedBy (PM user ID) is required")
        String actionedBy,

        /**
         * The specific Resource ID the PM is assigning.
         * Must match a resource in the resource module with availability Available or Allocated.
         */
        @NotBlank(message = "resourceId is required — PM must assign a specific resource on approval")
        String resourceId
) {}
