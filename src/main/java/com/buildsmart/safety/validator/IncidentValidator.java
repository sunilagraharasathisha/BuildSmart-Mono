package com.buildsmart.safety.validator;

import com.buildsmart.safety.web.dto.IncidentDtos.CreateIncidentRequest;
import org.springframework.stereotype.Component;

@Component
public class IncidentValidator {

    public void validate(CreateIncidentRequest request) {
        if (request.description().isBlank()) {
            throw new IllegalArgumentException("Incident description must not be blank");
        }
        if (request.description().length() > 5000) {
            throw new IllegalArgumentException("Incident description must not exceed 5000 characters");
        }
        if (request.projectId().isBlank()) {
            throw new IllegalArgumentException("Project ID must not be blank");
        }
        if (request.reportedBy().isBlank()) {
            throw new IllegalArgumentException("reportedBy (userId) must not be blank");
        }
        if (request.severity() == null) {
            throw new IllegalArgumentException("Incident severity is required");
        }
    }
}
