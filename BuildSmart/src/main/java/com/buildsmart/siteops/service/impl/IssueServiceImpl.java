package com.buildsmart.siteops.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.NotificationService;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.siteops.dto.IssueRequest;
import com.buildsmart.siteops.dto.IssueResponse;
import com.buildsmart.siteops.dto.IssueUpdateRequest;
import com.buildsmart.siteops.entity.Issue;
import com.buildsmart.siteops.enums.IssueSeverity;
import com.buildsmart.siteops.enums.IssueStatus;
import com.buildsmart.siteops.repository.IssueRepository;
import com.buildsmart.siteops.service.IssueService;
import com.buildsmart.siteops.validator.IssueValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Issue business logic.
 *
 * Construction workflow:
 *  1. Site Engineer reports an issue (linked optionally to a SiteLog).
 *  2. CRITICAL severity → immediate notification to Project Manager + auto-status ESCALATED.
 *  3. HIGH severity → notification to PM.
 *  4. PM assigns the issue to a user and tracks resolution.
 *  5. When resolved, site engineer marks it RESOLVED; PM closes it.
 *  6. Issue severity is immutable after creation (audit trail requirement).
 */
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository    issueRepository;
    private final ProjectRepository  projectRepository;
    private final IssueValidator     issueValidator;
    private final NotificationService notificationService;

    /* ════════════════════════════════════════════════════════════
       CREATE
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public IssueResponse createIssue(IssueRequest request) {

        issueValidator.validate(request);

        // ── Project existence ────────────────────────────────────────────────
        if (!projectRepository.existsById(request.projectId())) {
            throw new ResourceNotFoundException(
                    "Project not found: " + request.projectId());
        }

        // ── Build entity ─────────────────────────────────────────────────────
        String lastId = issueRepository.findTopByOrderByIssueIdDesc()
                .map(Issue::getIssueId).orElse(null);

        // CRITICAL issues are immediately escalated to avoid them sitting OPEN
        IssueStatus initialStatus = (request.severity() == IssueSeverity.CRITICAL)
                ? IssueStatus.ESCALATED
                : IssueStatus.OPEN;

        Issue issue = new Issue();
        issue.setIssueId(IdGeneratorUtil.nextIssueId(lastId));
        issue.setProjectId(request.projectId());
        issue.setLogId(request.logId());
        issue.setDescription(request.description());
        issue.setSeverity(request.severity());
        issue.setReportedBy(request.reportedBy());
        issue.setReportedAt(LocalDateTime.now());
        issue.setStatus(initialStatus);
        issue.setAssignedTo(request.assignedTo());

        Issue saved = issueRepository.save(issue);

        // ── Notifications based on severity ──────────────────────────────────
        if (saved.getSeverity() == IssueSeverity.CRITICAL) {
            // CRITICAL: immediate stop-work escalation — notify PM urgently
            notificationService.createNotification(
                    "PM-" + saved.getProjectId(),
                    saved.getIssueId(),
                    saved.getProjectId(),
                    String.format(
                            "🚨 CRITICAL ISSUE [%s] reported on project %s by %s. "
                            + "STOP-WORK condition may apply. Immediate action required! "
                            + "Description: %s",
                            saved.getIssueId(),
                            saved.getProjectId(),
                            saved.getReportedBy(),
                            truncate(saved.getDescription(), 200))
            );
        } else if (saved.getSeverity() == IssueSeverity.HIGH) {
            // HIGH: notify PM — address within the day
            notificationService.createNotification(
                    "PM-" + saved.getProjectId(),
                    saved.getIssueId(),
                    saved.getProjectId(),
                    String.format(
                            "⚠️ HIGH severity issue [%s] reported on project %s by %s. "
                            + "Please address within 24 hours. Description: %s",
                            saved.getIssueId(),
                            saved.getProjectId(),
                            saved.getReportedBy(),
                            truncate(saved.getDescription(), 200))
            );
        } else {
            // LOW / MEDIUM: notify PM as an FYI
            notificationService.createNotification(
                    "PM-" + saved.getProjectId(),
                    saved.getIssueId(),
                    saved.getProjectId(),
                    String.format(
                            "New %s issue [%s] reported on project %s. Description: %s",
                            saved.getSeverity(),
                            saved.getIssueId(),
                            saved.getProjectId(),
                            truncate(saved.getDescription(), 150))
            );
        }

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       READ
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional(readOnly = true)
    public IssueResponse getIssueById(String issueId) {
        return toResponse(find(issueId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return issueRepository.findByProjectIdOrderByReportedAtDesc(projectId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByProjectAndStatus(String projectId, IssueStatus status) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return issueRepository.findByProjectIdAndStatus(projectId, status)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByProjectAndSeverity(String projectId, IssueSeverity severity) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return issueRepository.findByProjectIdAndSeverity(projectId, severity)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByProjectAndReporter(String projectId, String reportedBy) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return issueRepository.findByProjectIdAndReportedBy(projectId, reportedBy)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByLogId(String logId) {
        return issueRepository.findByLogId(logId)
                .stream().map(this::toResponse).toList();
    }

    /* ════════════════════════════════════════════════════════════
       UPDATE
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public IssueResponse updateIssue(String issueId, IssueUpdateRequest request) {
        Issue existing = find(issueId);

        // Terminal state guard
        if (existing.getStatus() == IssueStatus.CLOSED) {
            throw new IllegalArgumentException(
                    "Issue " + issueId + " is CLOSED and cannot be modified.");
        }

        if (request.description() != null) {
            if (request.description().isBlank()) {
                throw new IllegalArgumentException("Description cannot be blank");
            }
            existing.setDescription(request.description());
        }

        // ── Validate assignedTo user if provided ────────────────────────────
        if (request.assignedTo() != null && !request.assignedTo().isBlank()) {
            issueValidator.validateUpdater(request.assignedTo(), "assignedTo");
            existing.setAssignedTo(request.assignedTo());
        }

        if (request.resolutionNotes() != null) {
            existing.setResolutionNotes(request.resolutionNotes());
        }

        if (request.status() != null) {
            validateStatusTransition(existing.getStatus(), request.status());

            // When marking RESOLVED, capture the resolution timestamp
            if (request.status() == IssueStatus.RESOLVED) {
                existing.setResolvedAt(LocalDateTime.now());

                // Notify the original reporter that the issue is resolved
                notificationService.createNotification(
                        existing.getReportedBy(),
                        existing.getIssueId(),
                        existing.getProjectId(),
                        String.format(
                                "Issue [%s] on project %s has been RESOLVED. Resolution notes: %s",
                                existing.getIssueId(),
                                existing.getProjectId(),
                                truncate(existing.getResolutionNotes(), 200))
                );
            }

            // When closing, notify the reporter
            if (request.status() == IssueStatus.CLOSED) {
                notificationService.createNotification(
                        existing.getReportedBy(),
                        existing.getIssueId(),
                        existing.getProjectId(),
                        String.format(
                                "Issue [%s] on project %s has been CLOSED by the Project Manager.",
                                existing.getIssueId(),
                                existing.getProjectId())
                );
            }

            existing.setStatus(request.status());
        }

        return toResponse(issueRepository.save(existing));
    }

    /* ════════════════════════════════════════════════════════════
       HELPERS
       ════════════════════════════════════════════════════════════ */

    private Issue find(String issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));
    }

    private void validateStatusTransition(IssueStatus current, IssueStatus target) {
        boolean allowed = switch (current) {
            case OPEN       -> target == IssueStatus.IN_PROGRESS
                            || target == IssueStatus.ESCALATED
                            || target == IssueStatus.CLOSED;
            case IN_PROGRESS -> target == IssueStatus.RESOLVED
                            || target == IssueStatus.ESCALATED
                            || target == IssueStatus.CLOSED;
            case ESCALATED  -> target == IssueStatus.IN_PROGRESS
                            || target == IssueStatus.RESOLVED
                            || target == IssueStatus.CLOSED;
            case RESOLVED   -> target == IssueStatus.CLOSED
                            || target == IssueStatus.IN_PROGRESS; // re-open if fix didn't hold
            case CLOSED     -> false; // terminal
        };
        if (!allowed) {
            throw new IllegalArgumentException(
                    "Invalid issue status transition: " + current + " → " + target);
        }
    }

    private IssueResponse toResponse(Issue i) {
        return new IssueResponse(
                i.getIssueId(),
                i.getProjectId(),
                i.getLogId(),
                i.getDescription(),
                i.getSeverity(),
                i.getReportedBy(),
                i.getReportedAt(),
                i.getStatus(),
                i.getAssignedTo(),
                i.getResolutionNotes(),
                i.getResolvedAt()
        );
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
