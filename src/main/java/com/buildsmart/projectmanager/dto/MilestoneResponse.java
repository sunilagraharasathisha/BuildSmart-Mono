package com.buildsmart.projectmanager.dto;

import com.buildsmart.common.enums.MilestoneStatus;

import java.time.LocalDate;

public record MilestoneResponse(
        String milestoneId,
        String projectId,
        String milestoneName,
        String description,
        LocalDate dueDate,
        LocalDate achievedDate,
        MilestoneStatus status
) {
}