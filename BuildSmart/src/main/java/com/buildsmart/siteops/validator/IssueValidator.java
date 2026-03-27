package com.buildsmart.siteops.validator;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.UserStatus;
import com.buildsmart.iam.repository.UserRepository;
import com.buildsmart.siteops.dto.IssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates Issue requests with full user identity checks against the IAM module.
 *
 * Covers:
 *  - reportedBy must be a real, ACTIVE user (SITE_ENGINEER, SAFETY_OFFICER, or ADMIN)
 *  - assignedTo (if provided) must be a real, ACTIVE user
 *  - description and severity are mandatory
 */
@Component
@RequiredArgsConstructor
public class IssueValidator {

    private final UserRepository userRepository;

    public void validate(IssueRequest request) {

        // ── Project ID ──────────────────────────────────────────────────────
        if (request.projectId() == null || request.projectId().isBlank()) {
            throw new IllegalArgumentException("Project ID is required.");
        }

        // ── Description ─────────────────────────────────────────────────────
        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("Issue description is required.");
        }

        // ── Severity ────────────────────────────────────────────────────────
        if (request.severity() == null) {
            throw new IllegalArgumentException(
                    "Severity is required. Allowed values: LOW, MEDIUM, HIGH, CRITICAL.");
        }

        // ── reportedBy — must exist, be ACTIVE, and have a valid reporting role ─
        if (request.reportedBy() == null || request.reportedBy().isBlank()) {
            throw new IllegalArgumentException(
                    "reportedBy (user ID) is required. This should be the Site Engineer's user ID.");
        }
        validateReporter(request.reportedBy());

        // ── assignedTo — optional, but if provided must be a real ACTIVE user ─
        if (request.assignedTo() != null && !request.assignedTo().isBlank()) {
            validateAssignee(request.assignedTo());
        }
    }

    /**
     * Validates the user updating an issue (status change, resolution, assignment).
     * Any active user on the project can be the updater.
     */
    public void validateUpdater(String userId, String fieldName) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(fieldName + " (user ID) is required.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: '" + userId + "'. "
                        + "The " + fieldName + " user ID does not exist. "
                        + "Check GET /api/v1/admin/users for valid user IDs."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is " + user.getStatus()
                    + " and cannot perform this action. Only ACTIVE users can update issues.");
        }
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    private void validateReporter(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: '" + userId + "' in field 'reportedBy'. "
                        + "This user ID does not exist in the system. "
                        + "Please check GET /api/v1/admin/users for valid IDs."));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is INACTIVE "
                    + "and cannot report issues. Contact Admin to reactivate this account.");
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is SUSPENDED "
                    + "and cannot report issues.");
        }
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") has not completed "
                    + "email verification. Please verify your account before reporting issues.");
        }
        // Roles allowed to report issues: SE, Safety Officer, PM, Admin
        if (user.getRole() == Role.VENDOR || user.getRole() == Role.FINANCE_OFFICER) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") has role "
                    + user.getRole() + ". Only SITE_ENGINEER, SAFETY_OFFICER, "
                    + "PROJECT_MANAGER, or ADMIN can report site issues.");
        }
    }

    private void validateAssignee(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignee user not found: '" + userId + "' in field 'assignedTo'. "
                        + "This user ID does not exist. "
                        + "Check GET /api/v1/admin/users for valid user IDs."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Assignee '" + userId + "' (" + user.getName() + ") is "
                    + user.getStatus() + ". Issues can only be assigned to ACTIVE users.");
        }
    }
}
