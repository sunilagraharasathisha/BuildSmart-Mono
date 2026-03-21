package com.buildsmart.projectmanager.validator;

import com.buildsmart.projectmanager.dto.TaskRequest;
import org.springframework.stereotype.Component;

@Component
public class TaskValidator {
    public void validate(TaskRequest request) {
        if (request.plannedEnd().isBefore(request.plannedStart())) {
            throw new IllegalArgumentException("plannedEnd must be after plannedStart");
        }
        if (request.actualStart() != null && request.actualEnd() != null && request.actualEnd().isBefore(request.actualStart())) {
            throw new IllegalArgumentException("actualEnd must be after actualStart");
        }
    }
}
