package com.buildsmart.projectmanager.validator;

import com.buildsmart.projectmanager.dto.ProjectRequest;
import com.buildsmart.projectmanager.exception.ProjectValidationException;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {
    public void validate(ProjectRequest request) {
        // Validate budget
        if (request.budget().signum() <= 0) {
            throw new ProjectValidationException("Budget must be greater than zero");
        }

        // Validate start date and end date
        if (request.startDate() == null) {
            throw new ProjectValidationException("Start date is required");
        }

        if (request.endDate() == null) {
            throw new ProjectValidationException("End date is required");
        }

        if (request.startDate().isEqual(request.endDate())) {
            throw new ProjectValidationException("Start date and end date cannot be the same");
        }

        if (request.endDate().isBefore(request.startDate())) {
            throw new ProjectValidationException("End date must be after start date");
        }

        // Additional validation: end date should not be too far in the future (optional business rule)
        if (request.endDate().isAfter(request.startDate().plusYears(5))) {
            throw new ProjectValidationException("Project duration cannot exceed 5 years");
        }
    }
}
