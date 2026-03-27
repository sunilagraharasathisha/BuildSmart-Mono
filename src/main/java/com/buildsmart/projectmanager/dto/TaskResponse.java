package com.buildsmart.projectmanager.dto;

import com.buildsmart.common.enums.Department;
import com.buildsmart.common.enums.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        String taskId,
        String projectId,
        Department assignedDepartment,
        String assignedTo,
        String description,
        LocalDate plannedStart,
        LocalDate plannedEnd,
        LocalDate actualStart,
        LocalDate actualEnd,
        TaskStatus status
) {
}
