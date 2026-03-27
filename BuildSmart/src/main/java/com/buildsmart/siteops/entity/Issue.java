package com.buildsmart.siteops.entity;

import com.buildsmart.siteops.enums.IssueSeverity;
import com.buildsmart.siteops.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a problem or risk observed at a construction site.
 * Issues can be standalone or linked to a SiteLog entry.
 * CRITICAL issues auto-trigger a notification to the Project Manager.
 */
@Getter
@Setter
@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @Column(name = "issue_id", nullable = false, updatable = false, length = 20)
    private String issueId;

    @Column(name = "project_id", nullable = false, length = 20)
    private String projectId;

    /**
     * Optional link to the SiteLog where this issue was first noted.
     */
    @Column(name = "log_id", length = 20)
    private String logId;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 15)
    private IssueSeverity severity;

    /**
     * IAM userId of the person who reported this issue.
     */
    @Column(name = "reported_by", nullable = false, length = 20)
    private String reportedBy;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private IssueStatus status;

    /**
     * IAM userId of the person assigned to resolve this issue.
     */
    @Column(name = "assigned_to", length = 20)
    private String assignedTo;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
