package com.buildsmart.siteops.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.NotificationService;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.siteops.dto.SiteLogRequest;
import com.buildsmart.siteops.dto.SiteLogResponse;
import com.buildsmart.siteops.dto.SiteLogReviewRequest;
import com.buildsmart.siteops.dto.SiteLogUpdateRequest;
import com.buildsmart.siteops.entity.SiteLog;
import com.buildsmart.siteops.repository.SiteLogRepository;
import com.buildsmart.siteops.service.SiteLogService;
import com.buildsmart.siteops.validator.SiteLogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Site Log business logic.
 *
 * Construction workflow:
 *  1. Site Engineer submits a daily log (today-only rule enforced).
 *  2. One log per project per day — prevents duplicate submissions.
 *  3. After submission the assigned Project Manager is notified automatically.
 *  4. PM can review and comment on the log.
 *  5. Progress percent must never decrease from the previous log
 *     (construction work is cumulative — you can't "un-build").
 */
@Service
@RequiredArgsConstructor
public class SiteLogServiceImpl implements SiteLogService {

    private final SiteLogRepository  siteLogRepository;
    private final ProjectRepository  projectRepository;
    private final SiteLogValidator   siteLogValidator;
    private final NotificationService notificationService;

    /* ════════════════════════════════════════════════════════════
       CREATE
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public SiteLogResponse createSiteLog(SiteLogRequest request) {

        // ── Structural validation ────────────────────────────────────────────
        siteLogValidator.validate(request);

        // ── Project existence ────────────────────────────────────────────────
        if (!projectRepository.existsById(request.projectId())) {
            throw new ResourceNotFoundException(
                    "Project not found: " + request.projectId()
                    + ". Please provide a valid project ID before creating a site log.");
        }

        // ── One-per-day uniqueness ────────────────────────────────────────────
        if (siteLogRepository.existsByProjectIdAndLogDate(request.projectId(), request.logDate())) {
            throw new DuplicateResourceException(
                    "A site log already exists for project " + request.projectId()
                    + " on " + request.logDate()
                    + ". You can only submit one log per project per day. Use the update endpoint to amend it.");
        }

        // ── Progress regression check ─────────────────────────────────────────
        // Construction is cumulative: today's progress cannot be less than yesterday's.
        siteLogRepository.findTopByProjectIdOrderByLogDateDesc(request.projectId())
                .ifPresent(prev -> {
                    if (request.progressPercent().compareTo(prev.getProgressPercent()) < 0) {
                        throw new IllegalArgumentException(
                                "Progress percent (" + request.progressPercent()
                                + "%) cannot be less than the previous log's progress ("
                                + prev.getProgressPercent()
                                + "%). Construction progress is cumulative.");
                    }
                });

        // ── Build & persist ───────────────────────────────────────────────────
        String lastId = siteLogRepository.findTopByOrderByLogIdDesc()
                .map(SiteLog::getLogId).orElse(null);

        SiteLog log = new SiteLog();
        log.setLogId(IdGeneratorUtil.nextSiteLogId(lastId));
        log.setProjectId(request.projectId());
        log.setLogDate(request.logDate());
        log.setActivities(request.activities());
        log.setIssuesSummary(request.issuesSummary());
        log.setProgressPercent(request.progressPercent());
        log.setSubmittedBy(request.submittedBy());
        log.setSubmittedAt(LocalDateTime.now());

        SiteLog saved = siteLogRepository.save(log);

        // ── Notify Project Manager that a daily log has been submitted ────────
        notificationService.createNotification(
                "PM-" + saved.getProjectId(),
                saved.getLogId(),
                saved.getProjectId(),
                String.format(
                        "Daily site log [%s] submitted by %s for project %s on %s. "
                        + "Progress: %s%%. Activities: %s",
                        saved.getLogId(),
                        saved.getSubmittedBy(),
                        saved.getProjectId(),
                        saved.getLogDate(),
                        saved.getProgressPercent(),
                        truncate(saved.getActivities(), 100))
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       READ
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional(readOnly = true)
    public SiteLogResponse getSiteLogById(String logId) {
        return toResponse(find(logId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteLogResponse> getSiteLogsByProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return siteLogRepository.findByProjectIdOrderByLogDateDesc(projectId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteLogResponse> getSiteLogsByProjectAndDateRange(
            String projectId, LocalDate from, LocalDate to) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates are required for range queries");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("'to' date must be on or after 'from' date");
        }
        return siteLogRepository.findByProjectIdAndLogDateBetweenOrderByLogDateDesc(projectId, from, to)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SiteLogResponse getSiteLogByProjectAndDate(String projectId, LocalDate date) {
        return toResponse(
                siteLogRepository.findByProjectIdAndLogDate(projectId, date)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No site log found for project " + projectId + " on " + date)));
    }

    @Override
    @Transactional(readOnly = true)
    public SiteLogResponse getLatestSiteLog(String projectId) {
        return toResponse(
                siteLogRepository.findTopByProjectIdOrderByLogDateDesc(projectId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No site logs found for project: " + projectId)));
    }

    /* ════════════════════════════════════════════════════════════
       UPDATE (partial — identity fields are immutable)
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public SiteLogResponse updateSiteLog(String logId, SiteLogUpdateRequest request) {
        SiteLog existing = find(logId);

        if (request.activities() != null) {
            existing.setActivities(request.activities());
        }
        if (request.issuesSummary() != null) {
            existing.setIssuesSummary(request.issuesSummary());
        }
        if (request.progressPercent() != null) {
            BigDecimal p = request.progressPercent();
            if (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Progress percent must be between 0 and 100");
            }
            // Regression check against the log BEFORE this one
            siteLogRepository.findByProjectIdAndLogDateBetweenOrderByLogDateDesc(
                            existing.getProjectId(),
                            LocalDate.MIN, existing.getLogDate().minusDays(1))
                    .stream().findFirst()
                    .ifPresent(prev -> {
                        if (p.compareTo(prev.getProgressPercent()) < 0) {
                            throw new IllegalArgumentException(
                                    "Updated progress (" + p
                                    + "%) cannot be less than the previous log's progress ("
                                    + prev.getProgressPercent() + "%).");
                        }
                    });
            existing.setProgressPercent(p);
        }

        return toResponse(siteLogRepository.save(existing));
    }

    /* ════════════════════════════════════════════════════════════
       REVIEW (Project Manager reviews the log)
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public SiteLogResponse reviewSiteLog(String logId, SiteLogReviewRequest request) {
        SiteLog existing = find(logId);

        // ── Validate reviewer is a real, ACTIVE PROJECT_MANAGER ─────────────
        siteLogValidator.validateReviewer(request.reviewedBy());

        if (existing.getReviewedBy() != null) {
            throw new IllegalArgumentException(
                    "Site log " + logId + " has already been reviewed by " + existing.getReviewedBy()
                    + ". A log can only be reviewed once.");
        }

        existing.setReviewedBy(request.reviewedBy());
        existing.setReviewedAt(LocalDateTime.now());
        existing.setReviewerComments(request.reviewerComments());

        SiteLog saved = siteLogRepository.save(existing);

        // ── Notify Site Engineer that their log was reviewed ──────────────────
        notificationService.createNotification(
                saved.getSubmittedBy(),
                saved.getLogId(),
                saved.getProjectId(),
                String.format(
                        "Your site log [%s] for project %s dated %s has been reviewed by %s. Comments: %s",
                        saved.getLogId(),
                        saved.getProjectId(),
                        saved.getLogDate(),
                        saved.getReviewedBy(),
                        truncate(saved.getReviewerComments(), 200))
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       HELPERS
       ════════════════════════════════════════════════════════════ */

    private SiteLog find(String logId) {
        return siteLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Site log not found: " + logId));
    }

    private SiteLogResponse toResponse(SiteLog s) {
        return new SiteLogResponse(
                s.getLogId(),
                s.getProjectId(),
                s.getLogDate(),
                s.getActivities(),
                s.getIssuesSummary(),
                s.getProgressPercent(),
                s.getSubmittedBy(),
                s.getSubmittedAt(),
                s.getReviewedBy(),
                s.getReviewedAt(),
                s.getReviewerComments()
        );
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
