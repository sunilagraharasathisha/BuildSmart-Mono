package com.buildsmart.projectmanager.validator;

import com.buildsmart.projectmanager.dto.MilestoneRequest;
import org.springframework.stereotype.Component;

@Component
public class MilestoneValidator {
    public void validate(MilestoneRequest request) {
        if (request.dueDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("dueDate cannot be in the past");
        }
        if (request.achievedDate() != null && request.achievedDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("achievedDate cannot be in the future");
        }
        if (request.achievedDate() != null && request.achievedDate().isAfter(request.dueDate())) {
            throw new IllegalArgumentException("achievedDate cannot be after dueDate");
        }
    }
}