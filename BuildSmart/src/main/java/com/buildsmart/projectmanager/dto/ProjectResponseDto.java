package com.buildsmart.projectmanager.dto;

import com.buildsmart.projectmanager.entity.Project;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProjectResponseDto {

    private String projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private Project.ProjectStatus status;
    private List<TaskResponseDto> tasks;
}
