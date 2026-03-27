package com.buildsmart.siteops.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Payload submitted by a Site Engineer to request Labor or Equipment for a project.
 *
 * Example:
 * {
 *   "projectId": "CHEBS26001",
 *   "requestedBy": "USR00003",
 *   "logId": "LOGBS001",
 *   "resourceType": "EQUIPMENT",
 *   "requirementDescription": "Need tower crane for lifting prefab slabs on Block B levels 4-6.",
 *   "requiredFrom": "2026-04-01",
 *   "requiredUntil": "2026-04-15"
 * }
 */
public record ResourceRequestCreateRequest(

        @NotBlank(message = "Project ID is required")
        String projectId,

        @NotBlank(message = "requestedBy (Site Engineer user ID) is required")
        String requestedBy,

        /** Optional: link to the daily log where the need was identified. */
        String logId,

        /**
         * Type of resource needed. Must be "LABOUR" or "EQUIPMENT".
         * The specific resource is chosen by the PM on approval.
         */
        @NotBlank(message = "resourceType is required: LABOUR or EQUIPMENT")
        String resourceType,

        @NotBlank(message = "Requirement description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String requirementDescription,

        @NotNull(message = "requiredFrom date is required")
        @FutureOrPresent(message = "requiredFrom must be today or a future date")
        LocalDate requiredFrom,

        /**
         * Optional end date. If omitted, allocation is open-ended until PM releases it.
         */
        LocalDate requiredUntil
) {}
