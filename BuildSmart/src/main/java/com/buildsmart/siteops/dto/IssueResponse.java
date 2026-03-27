package com.buildsmart.siteops.dto;

import com.buildsmart.siteops.enums.IssueSeverity;
import com.buildsmart.siteops.enums.IssueStatus;

import java.time.LocalDateTime;

public record IssueResponse(
        String issueId,
        String projectId,
        String logId,
        String description,
        IssueSeverity severity,
        String reportedBy,
        LocalDateTime reportedAt,
        IssueStatus status,
        String assignedTo,
        String resolutionNotes,
        LocalDateTime resolvedAt
) {}
