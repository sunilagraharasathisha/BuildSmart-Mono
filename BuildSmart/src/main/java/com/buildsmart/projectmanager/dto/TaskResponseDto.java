package com.buildsmart.projectmanager.dto;

import com.buildsmart.projectmanager.entity.Task;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskResponseDto {

    private String taskId;
    private String projectId;
    private Task.AssignedDepartment assignedDepartment;
    private String assignedTo;
    private String description;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    private LocalDate actualStart;
    private LocalDate actualEnd;
    private Task.TaskStatus status;
}
