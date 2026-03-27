package com.buildsmart.resource.validator;

import com.buildsmart.resource.dto.ResourceRequest;
import org.springframework.stereotype.Component;

@Component
public class ResourceValidator {

    public void validate(ResourceRequest request) {
        if (request.costRate() == null || request.costRate().signum() <= 0) {
            throw new IllegalArgumentException("Cost rate must be a positive number");
        }
        if (request.resourceName() == null || request.resourceName().isBlank()) {
            throw new IllegalArgumentException("Resource name is required");
        }
        if (request.type() == null) {
            throw new IllegalArgumentException("Resource type is required (Labor or Equipment)");
        }
        if (request.availability() == null) {
            throw new IllegalArgumentException("Availability status is required");
        }
    }
}
