package com.buildsmart.siteops.validator;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.UserStatus;
import com.buildsmart.iam.repository.UserRepository;
import com.buildsmart.siteops.dto.SiteLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Validates SiteLog requests with full user identity checks against the IAM module.
 *
 * Covers:
 *  - submittedBy must be a real, ACTIVE user with SITE_ENGINEER role
 *  - reviewedBy (in review requests) must be a real, ACTIVE PROJECT_MANAGER
 *  - logDate must be today (no back-dating or future entries)
 *  - progressPercent must be 0–100
 */
@Component
@RequiredArgsConstructor
public class SiteLogValidator {

    private final UserRepository userRepository;

    public void validate(SiteLogRequest request) {

        // ── Project ID ──────────────────────────────────────────────────────
        if (request.projectId() == null || request.projectId().isBlank()) {
            throw new IllegalArgumentException("Project ID is required.");
        }

        // ── Log date ────────────────────────────────────────────────────────
        if (request.logDate() == null) {
            throw new IllegalArgumentException("Log date is required.");
        }
        if (!request.logDate().isEqual(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Site logs can only be created for today's date ("
                    + LocalDate.now() + "). "
                    + "You provided: " + request.logDate() + ". "
                    + "Back-dated and future-dated entries are not allowed.");
        }

        // ── Progress percent ────────────────────────────────────────────────
        if (request.progressPercent() == null) {
            throw new IllegalArgumentException("progressPercent is required.");
        }
        BigDecimal p = request.progressPercent();
        if (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(
                    "progressPercent must be between 0 and 100. You provided: " + p);
        }

        // ── submittedBy — must exist, be ACTIVE, and be a SITE_ENGINEER ─────
        if (request.submittedBy() == null || request.submittedBy().isBlank()) {
            throw new IllegalArgumentException(
                    "submittedBy (Site Engineer user ID) is required.");
        }
        validateSiteEngineer(request.submittedBy(), "submittedBy");
    }

    /**
     * Validates that the reviewer is a real, ACTIVE PROJECT_MANAGER.
     * Called from SiteLogServiceImpl.reviewSiteLog().
     */
    public void validateReviewer(String reviewedBy) {
        if (reviewedBy == null || reviewedBy.isBlank()) {
            throw new IllegalArgumentException(
                    "reviewedBy (Project Manager user ID) is required.");
        }
        User user = userRepository.findById(reviewedBy)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: '" + reviewedBy + "'. "
                        + "The reviewedBy user ID does not exist. "
                        + "Please check GET /api/v1/admin/users for valid user IDs."));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "User '" + reviewedBy + "' (" + user.getName() + ") is INACTIVE "
                    + "and cannot perform reviews. Contact Admin to reactivate this account.");
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException(
                    "User '" + reviewedBy + "' (" + user.getName() + ") is SUSPENDED "
                    + "and cannot perform reviews.");
        }
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new IllegalArgumentException(
                    "User '" + reviewedBy + "' (" + user.getName() + ") has not completed "
                    + "email verification and cannot perform reviews.");
        }
        if (user.getRole() != Role.PROJECT_MANAGER && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "User '" + reviewedBy + "' (" + user.getName() + ") has role "
                    + user.getRole() + ". Only PROJECT_MANAGER or ADMIN can review site logs. "
                    + "Site Engineers cannot review their own logs.");
        }
    }

    // ── Internal helper ──────────────────────────────────────────────────────

    private void validateSiteEngineer(String userId, String fieldName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: '" + userId + "' in field '" + fieldName + "'. "
                        + "This user ID does not exist in the system. "
                        + "Please check GET /api/v1/admin/users for valid IDs."));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is INACTIVE "
                    + "and cannot submit site logs. Contact Admin to reactivate.");
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is SUSPENDED "
                    + "and cannot submit site logs.");
        }
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") has not completed "
                    + "email verification. Please verify your account before submitting logs.");
        }
        if (user.getRole() != Role.SITE_ENGINEER && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") has role "
                    + user.getRole() + ". Only SITE_ENGINEER can submit site logs. "
                    + "If you are a Site Engineer, please use your own user ID.");
        }
    }
}
