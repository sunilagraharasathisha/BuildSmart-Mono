package com.buildsmart.projectmanager.dto;

import com.buildsmart.common.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResponse(
        String projectId,
        String projectName,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        ProjectStatus status
) {
}
