package com.buildsmart.projectmanager.dto;

import com.buildsmart.common.enums.Department;
import com.buildsmart.common.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank String projectId,
        @NotNull Department assignedDepartment,
        @NotBlank String assignedTo,
        @NotBlank String description,
        @NotNull LocalDate plannedStart,
        @NotNull LocalDate plannedEnd,
        LocalDate actualStart,
        LocalDate actualEnd,
        @NotNull TaskStatus status
) {
}
