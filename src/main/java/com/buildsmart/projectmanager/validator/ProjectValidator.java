package com.buildsmart.projectmanager.validator;

import com.buildsmart.projectmanager.dto.ProjectRequest;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {
    public void validate(ProjectRequest request) {
        if (request.startDate().isEqual(request.endDate())) {
            throw new IllegalArgumentException("startDate and endDate must not be equal");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (request.budget().signum() <= 0) {
            throw new IllegalArgumentException("budget must be greater than zero");
        }
    }
}
