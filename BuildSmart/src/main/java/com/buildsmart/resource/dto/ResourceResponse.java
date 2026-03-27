package com.buildsmart.resource.dto;

import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;

import java.math.BigDecimal;

public record ResourceResponse(
        String resourceId,
        String resourceName,
        ResourceType type,
        ResourceAvailability availability,
        BigDecimal costRate,
        String description
) {}
