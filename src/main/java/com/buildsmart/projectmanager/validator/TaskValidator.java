package com.buildsmart.projectmanager.validator;

import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.repository.UserRepository;
import com.buildsmart.projectmanager.dto.TaskRequest;
import com.buildsmart.projectmanager.exception.TaskValidationException;
import com.buildsmart.projectmanager.exception.InvalidUserDepartmentException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.exception.SameDateException;
import com.buildsmart.common.enums.Department;
import com.buildsmart.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

        // Validate that user's role matches the assigned department
        if (!isUserInDepartment(user, request.assignedDepartment())) {
            throw new InvalidUserDepartmentException(
                    String.format("User %s does not belong to department %s",
                            request.assignedTo(), request.assignedDepartment())
            );
        }

        // Validate dates
        if (request.plannedStart() == null || request.plannedEnd() == null) {
            throw new TaskValidationException("Planned start and end dates are required");
        }

        // Check if plannedStart and plannedEnd are the same
        if (request.plannedStart().isEqual(request.plannedEnd())) {
            throw new SameDateException("Planned start date and planned end date cannot be the same");
        }

        if (request.plannedEnd().isBefore(request.plannedStart())) {
            throw new TaskValidationException("Planned end date must be after planned start date");
        }

        if (request.actualStart() != null && request.actualEnd() != null && request.actualEnd().isBefore(request.actualStart())) {
            throw new TaskValidationException("Actual end date must be after actual start date");
        }
    }

    /**
     * Check if user's role corresponds to the assigned department
     */
    private boolean isUserInDepartment(User user, Department department) {
        Role userRole = user.getRole();
        
        return switch (department) {
            case FINANCE -> userRole == Role.FINANCE_OFFICER;
            case VENDOR -> userRole == Role.VENDOR;
            case SAFETY -> userRole == Role.SAFETY_OFFICER;
            case SITE -> userRole == Role.SITE_ENGINEER || userRole == Role.PROJECT_MANAGER;
            default -> false;
        };
    }
}
