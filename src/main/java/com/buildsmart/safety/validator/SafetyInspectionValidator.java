package com.buildsmart.safety.validator;

import com.buildsmart.safety.web.dto.InspectionDtos.CreateInspectionRequest;
import org.springframework.stereotype.Component;

@Component
public class SafetyInspectionValidator {

    public void validate(CreateInspectionRequest request) {
        if (request.projectId().isBlank()) {
            throw new IllegalArgumentException("Project ID must not be blank");
        }
        if (request.officerId().isBlank()) {
            throw new IllegalArgumentException("Officer ID (userId) must not be blank");
        }
        if (request.findings() != null && request.findings().length() > 5000) {
            throw new IllegalArgumentException("Findings must not exceed 5000 characters");
        }
    }
}
