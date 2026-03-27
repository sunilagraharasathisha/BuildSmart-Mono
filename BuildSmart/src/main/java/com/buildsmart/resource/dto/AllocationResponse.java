package com.buildsmart.resource.dto;

import com.buildsmart.resource.enums.AllocationStatus;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AllocationResponse(
        String allocationId,
        String projectId,
        String resourceId,
        String resourceName,
        ResourceType resourceType,
        ResourceAvailability resourceAvailability,
        BigDecimal resourceCostRate,
        LocalDate assignedDate,
        LocalDate releasedDate,
        AllocationStatus status,
        String requestNotes
) {}
