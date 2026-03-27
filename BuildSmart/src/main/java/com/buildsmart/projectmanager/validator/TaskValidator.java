package com.buildsmart.projectmanager.validator;

import com.buildsmart.iam.entity.User;
import com.buildsmart.projectmanager.dto.TaskRequest;
import com.buildsmart.projectmanager.exception.TaskValidationException;
import com.buildsmart.projectmanager.exception.InvalidUserDepartmentException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.buildsmart.iam.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class TaskValidator {

    private final UserRepository userRepository;

    public void validate(TaskRequest request) {
        // Validate required fields
        if (request.projectId() == null || request.projectId().trim().isEmpty()) {
            throw new TaskValidationException("Project ID is required");
        }

        if (request.assignedDepartment() == null) {
            throw new TaskValidationException("Assigned department is required");
        }


        if (request.assignedTo() == null || request.assignedTo().isBlank()) {
            throw new TaskValidationException("Assigned user cannot be empty");
        }

        User user = userRepository.findById(request.assignedTo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + request.assignedTo()
                ));

        // ✅ Check if user belongs to required department
        if (!user.getRole().equals(request.assignedDepartment())) {
            throw new InvalidUserDepartmentException(
                    String.format("User %s does not belong to department %s",
                            request.assignedTo(), request.assignedDepartment())
            );
        }

        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new TaskValidationException("Description is required");
        }

        if (request.status() == null) {
            throw new TaskValidationException("Task status is required");
        }

        // Validate planned dates
        if (request.plannedStart() == null) {
            throw new TaskValidationException("Planned start date is required");
        }

        if (request.plannedEnd() == null) {
            throw new TaskValidationException("Planned end date is required");
        }

        if (request.plannedStart().isEqual(request.plannedEnd())) {
            throw new TaskValidationException("Planned start date and planned end date cannot be the same");
        }

        if (request.plannedEnd().isBefore(request.plannedStart())) {
            throw new TaskValidationException("Planned end date must be after planned start date");
        }

        // Validate actual dates if provided
        if (request.actualStart() != null) {
            if (request.actualStart().isEqual(request.plannedStart())) {
                throw new TaskValidationException("Actual start date cannot be the same as planned start date");
            }

            if (request.actualStart().isBefore(request.plannedStart())) {
                throw new TaskValidationException("Actual start date must be after planned start date");
            }

            // Validate actual end date if both actual dates are provided
            if (request.actualEnd() != null) {
                if (request.actualStart().isEqual(request.actualEnd())) {
                    throw new TaskValidationException("Actual start date and actual end date cannot be the same");
                }

                if (request.actualEnd().isBefore(request.actualStart())) {
                    throw new TaskValidationException("Actual end date must be after actual start date");
                }

                // Additional validation: actual end should not be before planned end (optional business rule)
                if (request.actualEnd().isBefore(request.plannedEnd())) {
                    throw new TaskValidationException("Actual end date should not be before planned end date");
                }
            }
        }

        // If actual end is provided but actual start is not, that's invalid
        if (request.actualEnd() != null && request.actualStart() == null) {
            throw new TaskValidationException("Actual start date is required when actual end date is provided");
        }

        // Business rule: planned duration should not exceed 1 year
        if (request.plannedEnd().isAfter(request.plannedStart().plusYears(1))) {
            throw new TaskValidationException("Task planned duration cannot exceed 1 year");
        }
    }
}
