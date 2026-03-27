package com.buildsmart.siteops.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SiteLogResponse(
        String logId,
        String projectId,
        LocalDate logDate,
        String activities,
        String issuesSummary,
        BigDecimal progressPercent,
        String submittedBy,
        LocalDateTime submittedAt,
        String reviewedBy,
        LocalDateTime reviewedAt,
        String reviewerComments
) {}
