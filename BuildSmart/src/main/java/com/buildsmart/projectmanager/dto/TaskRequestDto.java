package com.buildsmart.projectmanager.dto;

import com.buildsmart.projectmanager.entity.Task;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDto {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotNull(message = "Assigned department is required")
    private Task.AssignedDepartment assignedDepartment;

    @Size(max = 100)
    private String assignedTo;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Planned start date is required")
    private LocalDate plannedStart;

    @NotNull(message = "Planned end date is required")
    private LocalDate plannedEnd;

    private LocalDate actualStart;
    private LocalDate actualEnd;
}
