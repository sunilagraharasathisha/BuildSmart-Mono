package com.buildsmart.siteops.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A daily log entry submitted by a Site Engineer for a specific project.
 * Only one log is allowed per project per calendar day (enforced in service).
 * Progress percent is cumulative (0–100).
 */
@Getter
@Setter
@Entity
@Table(name = "site_logs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_site_log_project_date",
                columnNames = {"project_id", "log_date"}))
public class SiteLog {

    @Id
    @Column(name = "log_id", nullable = false, updatable = false, length = 20)
    private String logId;

    /**
     * FK to Project (String ID matching projects table).
     */
    @Column(name = "project_id", nullable = false, length = 20)
    private String projectId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "activities", columnDefinition = "TEXT")
    private String activities;

    /**
     * Brief summary of issues noted in the log (free text; detailed issues tracked in Issue entity).
     */
    @Column(name = "issues_summary", columnDefinition = "TEXT")
    private String issuesSummary;

    /**
     * Cumulative project progress as of this log date (0.00–100.00).
     */
    @Column(name = "progress_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercent;

    /**
     * IAM userId of the site engineer who submitted this log.
     */
    @Column(name = "submitted_by", nullable = false, length = 20)
    private String submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    /**
     * IAM userId of the reviewer (PM or senior engineer). Nullable until reviewed.
     */
    @Column(name = "reviewed_by", length = 20)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewer_comments", columnDefinition = "TEXT")
    private String reviewerComments;
}
