package com.buildsmart.projectmanager.dto;

import com.buildsmart.projectmanager.entity.Project;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectRequestDto {

    @NotBlank(message = "Project name is required")
    @Size(max = 150)
    private String projectName;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be greater than zero")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal budget;

    @NotNull(message = "Status is required")
    private Project.ProjectStatus status;
}
