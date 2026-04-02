package com.buildsmart.projectmanager.dto;

import com.buildsmart.common.enums.MilestoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MilestoneRequest(
        @NotBlank String projectId,
        @NotBlank String milestoneName,
        @NotBlank String description,
        @NotNull LocalDate dueDate,
        LocalDate achievedDate,
        @NotNull MilestoneStatus status
) {
}