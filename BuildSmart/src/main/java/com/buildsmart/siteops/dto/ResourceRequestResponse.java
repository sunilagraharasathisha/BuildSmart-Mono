package com.buildsmart.siteops.dto;

import com.buildsmart.siteops.enums.ResourceRequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response record for a ResourceRequest, returned by all read and write endpoints.
 */
public record ResourceRequestResponse(
        String requestId,
        String projectId,
        String requestedBy,
        String logId,
        String resourceType,
        /** Populated only after PM approves and assigns a specific resource. */
        String resourceId,
        String requirementDescription,
        LocalDate requiredFrom,
        LocalDate requiredUntil,
        ResourceRequestStatus status,
        /** Populated if PM rejected the request. */
        String rejectionReason,
        /** Populated once an Allocation is auto-created on approval. */
        String allocationId,
        LocalDateTime requestedAt,
        LocalDateTime actionedAt,
        String actionedBy
) {}
