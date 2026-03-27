package com.buildsmart.siteops.entity;

import com.buildsmart.siteops.enums.ResourceRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A Site Engineer's formal request for a Labor worker or Equipment unit on a project.
 *
 * Construction workflow:
 *  1. SE submits this request describing what resource they need and why.
 *  2. PM reviews the request in Swagger / front-end.
 *  3. PM APPROVES → the system auto-creates an Active Allocation in the Resource module.
 *  4. PM REJECTS  → rejectionReason is stored; SE is notified.
 *  5. PM RELEASES → when the work requiring that resource is complete.
 *  6. SE can CANCEL a PENDING request before PM acts.
 *
 * Linking:
 *  - projectId  : links to Project (cross-module, stored as string).
 *  - resourceId : links to ResourceEntity (cross-module, stored as string); null until PM assigns.
 *  - logId      : optional link to the SiteLog entry where the need was identified.
 *  - allocationId: populated once PM approves and allocation is created.
 */
@Getter
@Setter
@Entity
@Table(name = "resource_requests")
public class ResourceRequestEntity {

    @Id
    @Column(name = "request_id", nullable = false, updatable = false, length = 20)
    private String requestId;

    /** Project this resource is needed for. */
    @Column(name = "project_id", nullable = false, length = 20)
    private String projectId;

    /**
     * IAM userId of the Site Engineer raising the request.
     * Stored for accountability and notifications.
     */
    @Column(name = "requested_by", nullable = false, length = 20)
    private String requestedBy;

    /**
     * Optional link to the SiteLog entry where the resource need was identified.
     */
    @Column(name = "log_id", length = 20)
    private String logId;

    /**
     * Resource type requested: LABOUR or EQUIPMENT.
     * The SE specifies the type; the PM selects the specific resource on approval.
     */
    @Column(name = "resource_type", nullable = false, length = 20)
    private String resourceType;

    /**
     * Specific resource ID chosen by the PM on approval.
     * Null until approved.
     */
    @Column(name = "resource_id", length = 20)
    private String resourceId;

    /**
     * Human-readable description of what is needed.
     * e.g. "Need 1 tower crane for lifting steel beams on Block C, Level 3-5"
     */
    @Column(name = "requirement_description", nullable = false, columnDefinition = "TEXT")
    private String requirementDescription;

    /** Date the resource is needed from. */
    @Column(name = "required_from", nullable = false)
    private LocalDate requiredFrom;

    /** Optional: expected date the resource will be released. */
    @Column(name = "required_until")
    private LocalDate requiredUntil;

    /** Current status in the approval lifecycle. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private ResourceRequestStatus status;

    /** PM's reason if the request is rejected. Null otherwise. */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * Allocation ID created in the Resource module when PM approves.
     * Null until approved.
     */
    @Column(name = "allocation_id", length = 20)
    private String allocationId;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** Timestamp when PM acted (approved/rejected/released). */
    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    /**
     * IAM userId of the PM who approved/rejected this request.
     * Null until actioned.
     */
    @Column(name = "actioned_by", length = 20)
    private String actionedBy;
}
