package com.buildsmart.siteops.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.NotificationService;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.iam.entity.Role;
import com.buildsmart.iam.entity.User;
import com.buildsmart.iam.entity.UserStatus;
import com.buildsmart.iam.repository.UserRepository;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.resource.entity.AllocationEntity;
import com.buildsmart.resource.entity.ResourceEntity;
import com.buildsmart.resource.enums.AllocationStatus;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;
import com.buildsmart.resource.repository.AllocationRepository;
import com.buildsmart.resource.repository.ResourceRepository;
import com.buildsmart.siteops.dto.ResourceRequestApprovalRequest;
import com.buildsmart.siteops.dto.ResourceRequestCreateRequest;
import com.buildsmart.siteops.dto.ResourceRequestRejectionRequest;
import com.buildsmart.siteops.dto.ResourceRequestResponse;
import com.buildsmart.siteops.entity.ResourceRequestEntity;
import com.buildsmart.siteops.enums.ResourceRequestStatus;
import com.buildsmart.siteops.repository.ResourceRequestRepository;
import com.buildsmart.siteops.repository.SiteLogRepository;
import com.buildsmart.siteops.service.ResourceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resource Request business logic.
 *
 * Real-world construction scenario this implements:
 * ─────────────────────────────────────────────────
 * Day 1  – Site Engineer starts a daily SiteLog for Block C foundation work.
 *          While logging activities, they realize they need a Tower Crane and
 *          3 masons from next Monday. They submit a ResourceRequest (type=EQUIPMENT
 *          and a second request type=LABOUR).
 *
 * Day 1  – Project Manager receives a notification: "Resource request pending approval."
 *          PM opens Swagger, reviews available resources, picks RESBS003 (Tower Crane #2)
 *          and approves. The system auto-creates an Active Allocation (ALCBS00X) in the
 *          Resource module, updates the crane's availability to Allocated, and notifies
 *          the SE.
 *
 * Day 8  – Work is complete. PM calls releaseRequest → Allocation transitions to Released,
 *          crane availability returns to Available, SE is notified.
 *
 * Status transitions:
 *   PENDING  → APPROVED  (PM approves, picks specific resource)
 *   PENDING  → REJECTED  (PM rejects with reason)
 *   PENDING  → CANCELLED (SE withdraws before PM acts)
 *   APPROVED → RELEASED  (PM releases when work is done)
 */
@Service
@RequiredArgsConstructor
public class ResourceRequestServiceImpl implements ResourceRequestService {

    private final ResourceRequestRepository  resourceRequestRepository;
    private final ProjectRepository          projectRepository;
    private final ResourceRepository         resourceRepository;
    private final AllocationRepository       allocationRepository;
    private final SiteLogRepository          siteLogRepository;
    private final NotificationService        notificationService;
    private final UserRepository             userRepository;

    /* ════════════════════════════════════════════════════════════
       CREATE — Site Engineer submits a resource request
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public ResourceRequestResponse createRequest(ResourceRequestCreateRequest request) {

        // ── Project must exist ──────────────────────────────────────────────
        if (!projectRepository.existsById(request.projectId())) {
            throw new ResourceNotFoundException(
                    "Project not found: " + request.projectId()
                    + ". Please provide a valid project ID.");
        }

        // ── requestedBy must be a real, ACTIVE SITE_ENGINEER ───────────────
        validateSiteEngineerUser(request.requestedBy(), "requestedBy");

        // ── Validate resource type string ───────────────────────────────────
        ResourceType resourceType = parseResourceType(request.resourceType());

        // ── If logId provided, it must exist ────────────────────────────────
        if (request.logId() != null && !request.logId().isBlank()) {
            if (!siteLogRepository.existsById(request.logId())) {
                throw new ResourceNotFoundException(
                        "Site log not found: " + request.logId()
                        + ". If linking to a log, it must exist first.");
            }
        }

        // ── Date sanity: requiredUntil must be >= requiredFrom ───────────────
        if (request.requiredUntil() != null
                && request.requiredUntil().isBefore(request.requiredFrom())) {
            throw new IllegalArgumentException(
                    "requiredUntil must be on or after requiredFrom ("
                    + request.requiredFrom() + ").");
        }

        // ── Build entity ─────────────────────────────────────────────────────
        String lastId = resourceRequestRepository.findTopByOrderByRequestIdDesc()
                .map(ResourceRequestEntity::getRequestId).orElse(null);

        ResourceRequestEntity entity = new ResourceRequestEntity();
        entity.setRequestId(IdGeneratorUtil.nextResourceRequestId(lastId));
        entity.setProjectId(request.projectId());
        entity.setRequestedBy(request.requestedBy());
        entity.setLogId((request.logId() != null && !request.logId().isBlank())
                ? request.logId() : null);
        entity.setResourceType(resourceType.name());
        entity.setRequirementDescription(request.requirementDescription());
        entity.setRequiredFrom(request.requiredFrom());
        entity.setRequiredUntil(request.requiredUntil());
        entity.setStatus(ResourceRequestStatus.PENDING);
        entity.setRequestedAt(LocalDateTime.now());

        ResourceRequestEntity saved = resourceRequestRepository.save(entity);

        // ── Notify PM: new resource request needs attention ──────────────────
        notificationService.createNotification(
                "PM-" + saved.getProjectId(),
                saved.getRequestId(),
                saved.getProjectId(),
                String.format(
                        "📋 Resource Request [%s] raised by %s for project %s. "
                        + "Type: %s. Needed from: %s%s. Description: %s",
                        saved.getRequestId(),
                        saved.getRequestedBy(),
                        saved.getProjectId(),
                        saved.getResourceType(),
                        saved.getRequiredFrom(),
                        saved.getRequiredUntil() != null
                                ? " until " + saved.getRequiredUntil() : " (open-ended)",
                        truncate(saved.getRequirementDescription(), 150))
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       READ
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional(readOnly = true)
    public ResourceRequestResponse getRequestById(String requestId) {
        return toResponse(find(requestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequestResponse> getRequestsByProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return resourceRequestRepository
                .findByProjectIdOrderByRequestedAtDesc(projectId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequestResponse> getPendingRequestsByProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return resourceRequestRepository
                .findByProjectIdAndStatusOrderByRequestedAtAsc(
                        projectId, ResourceRequestStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequestResponse> getRequestsByEngineer(String requestedBy) {
        return resourceRequestRepository
                .findByRequestedByOrderByRequestedAtDesc(requestedBy)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceRequestResponse> getRequestsByLog(String logId) {
        return resourceRequestRepository
                .findByLogIdOrderByRequestedAtDesc(logId)
                .stream().map(this::toResponse).toList();
    }

    /* ════════════════════════════════════════════════════════════
       APPROVE — PM picks a specific resource and approves
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public ResourceRequestResponse approveRequest(String requestId,
                                                   ResourceRequestApprovalRequest approvalRequest) {
        ResourceRequestEntity req = find(requestId);

        // ── Only PENDING can be approved ────────────────────────────────────
        if (req.getStatus() != ResourceRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only PENDING requests can be approved. "
                    + "Request " + requestId + " is currently " + req.getStatus() + ".");
        }

        // ── actionedBy must be a real, ACTIVE PROJECT_MANAGER ──────────────
        validateProjectManagerUser(approvalRequest.actionedBy(), "actionedBy");

        // ── Validate the resource selected by PM ────────────────────────────
        ResourceEntity resource = resourceRepository.findById(approvalRequest.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found: " + approvalRequest.resourceId()
                        + ". Please choose a valid resource ID from GET /api/v1/resources"));

        // ── Resource must be Available or Allocated (not Unavailable/Maintenance) ─
        if (resource.getAvailability() == ResourceAvailability.Unavailable
                || resource.getAvailability() == ResourceAvailability.Maintenance) {
            throw new IllegalArgumentException(
                    "Resource '" + resource.getResourceName()
                    + "' is currently " + resource.getAvailability()
                    + " and cannot be assigned. Please pick an Available resource.");
        }

        // ── Resource type must match what was requested ─────────────────────
        ResourceType requestedType = parseResourceType(req.getResourceType());
        if (resource.getType() != requestedType) {
            throw new IllegalArgumentException(
                    "Type mismatch: Site Engineer requested " + requestedType
                    + " but resource '" + resource.getResourceName()
                    + "' is of type " + resource.getType()
                    + ". Please assign a resource of type " + requestedType + ".");
        }

        // ── Auto-create an Active Allocation in the Resource module ──────────
        String lastAllocId = allocationRepository.findTopByOrderByAllocationIdDesc()
                .map(AllocationEntity::getAllocationId).orElse(null);

        AllocationEntity allocation = new AllocationEntity();
        allocation.setAllocationId(IdGeneratorUtil.nextAllocationId(lastAllocId));
        allocation.setProjectId(req.getProjectId());
        allocation.setResource(resource);
        allocation.setAssignedDate(req.getRequiredFrom());
        allocation.setReleasedDate(req.getRequiredUntil());
        allocation.setStatus(AllocationStatus.Active);
        allocation.setRequestNotes(
                "Auto-created from ResourceRequest " + req.getRequestId()
                + " raised by " + req.getRequestedBy()
                + ". Requirement: " + truncate(req.getRequirementDescription(), 200));

        AllocationEntity savedAllocation = allocationRepository.save(allocation);

        // ── Mark resource as Allocated ───────────────────────────────────────
        resource.setAvailability(ResourceAvailability.Allocated);
        resourceRepository.save(resource);

        // ── Update the request ───────────────────────────────────────────────
        req.setStatus(ResourceRequestStatus.APPROVED);
        req.setResourceId(approvalRequest.resourceId());
        req.setAllocationId(savedAllocation.getAllocationId());
        req.setActionedBy(approvalRequest.actionedBy());
        req.setActionedAt(LocalDateTime.now());

        ResourceRequestEntity saved = resourceRequestRepository.save(req);

        // ── Notify Site Engineer: request approved ───────────────────────────
        notificationService.createNotification(
                saved.getRequestedBy(),
                saved.getRequestId(),
                saved.getProjectId(),
                String.format(
                        "✅ Your resource request [%s] for project %s has been APPROVED by %s. "
                        + "Assigned resource: '%s' (%s). Available from: %s. "
                        + "Allocation ID: %s",
                        saved.getRequestId(),
                        saved.getProjectId(),
                        saved.getActionedBy(),
                        resource.getResourceName(),
                        resource.getType(),
                        saved.getRequiredFrom(),
                        saved.getAllocationId())
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       REJECT — PM rejects with a mandatory reason
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public ResourceRequestResponse rejectRequest(String requestId,
                                                  ResourceRequestRejectionRequest rejectionRequest) {
        ResourceRequestEntity req = find(requestId);

        // ── Only PENDING can be rejected ────────────────────────────────────
        if (req.getStatus() != ResourceRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only PENDING requests can be rejected. "
                    + "Request " + requestId + " is currently " + req.getStatus() + ".");
        }

        // ── actionedBy must be a real, ACTIVE PROJECT_MANAGER ──────────────
        validateProjectManagerUser(rejectionRequest.actionedBy(), "actionedBy");

        req.setStatus(ResourceRequestStatus.REJECTED);
        req.setRejectionReason(rejectionRequest.rejectionReason());
        req.setActionedBy(rejectionRequest.actionedBy());
        req.setActionedAt(LocalDateTime.now());

        ResourceRequestEntity saved = resourceRequestRepository.save(req);

        // ── Notify Site Engineer: request rejected ───────────────────────────
        notificationService.createNotification(
                saved.getRequestedBy(),
                saved.getRequestId(),
                saved.getProjectId(),
                String.format(
                        "❌ Your resource request [%s] for project %s has been REJECTED by %s. "
                        + "Reason: %s. "
                        + "Please submit a new request or discuss alternatives with your PM.",
                        saved.getRequestId(),
                        saved.getProjectId(),
                        saved.getActionedBy(),
                        saved.getRejectionReason())
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       RELEASE — PM marks resource work as done, frees the resource
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public ResourceRequestResponse releaseRequest(String requestId, String actionedBy) {
        ResourceRequestEntity req = find(requestId);

        // ── Only APPROVED requests can be released ──────────────────────────
        if (req.getStatus() != ResourceRequestStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Only APPROVED requests can be released. "
                    + "Request " + requestId + " is currently " + req.getStatus() + ".");
        }

        // ── actionedBy must be a real, ACTIVE PROJECT_MANAGER ──────────────
        validateProjectManagerUser(actionedBy, "actionedBy");

        // ── Release the underlying Allocation ────────────────────────────────
        if (req.getAllocationId() != null) {
            allocationRepository.findById(req.getAllocationId()).ifPresent(alloc -> {
                if (alloc.getStatus() == AllocationStatus.Active) {
                    alloc.setStatus(AllocationStatus.Released);
                    alloc.setReleasedDate(
                            alloc.getReleasedDate() != null
                                    ? alloc.getReleasedDate()
                                    : java.time.LocalDate.now());
                    allocationRepository.save(alloc);

                    // Free up the resource availability
                    ResourceEntity resource = alloc.getResource();
                    if (resource.getAvailability() == ResourceAvailability.Allocated) {
                        resource.setAvailability(ResourceAvailability.Available);
                        resourceRepository.save(resource);
                    }
                }
            });
        }

        req.setStatus(ResourceRequestStatus.RELEASED);
        req.setActionedBy(actionedBy);
        req.setActionedAt(LocalDateTime.now());

        ResourceRequestEntity saved = resourceRequestRepository.save(req);

        // ── Notify Site Engineer: resource released ──────────────────────────
        notificationService.createNotification(
                saved.getRequestedBy(),
                saved.getRequestId(),
                saved.getProjectId(),
                String.format(
                        "🔓 Resource for request [%s] on project %s has been RELEASED by %s. "
                        + "The resource is now available for other projects.",
                        saved.getRequestId(),
                        saved.getProjectId(),
                        actionedBy)
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       CANCEL — Site Engineer withdraws a pending request
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public ResourceRequestResponse cancelRequest(String requestId, String cancelledBy) {
        ResourceRequestEntity req = find(requestId);

        // ── Only PENDING requests can be cancelled ──────────────────────────
        if (req.getStatus() != ResourceRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only PENDING requests can be cancelled by the Site Engineer. "
                    + "Request " + requestId + " is currently " + req.getStatus()
                    + ". If the request is APPROVED, ask your PM to release it.");
        }

        // ── cancelledBy must be a real, ACTIVE SITE_ENGINEER ───────────────
        validateSiteEngineerUser(cancelledBy, "cancelledBy");

        req.setStatus(ResourceRequestStatus.CANCELLED);
        req.setActionedBy(cancelledBy);
        req.setActionedAt(LocalDateTime.now());

        ResourceRequestEntity saved = resourceRequestRepository.save(req);

        // ── Notify PM that the SE withdrew the request ───────────────────────
        notificationService.createNotification(
                "PM-" + saved.getProjectId(),
                saved.getRequestId(),
                saved.getProjectId(),
                String.format(
                        "🚫 Resource request [%s] on project %s has been CANCELLED by %s. "
                        + "No action required.",
                        saved.getRequestId(),
                        saved.getProjectId(),
                        cancelledBy)
        );

        return toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       HELPERS
       ════════════════════════════════════════════════════════════ */

    private ResourceRequestEntity find(String requestId) {
        return resourceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource request not found: " + requestId));
    }

    private ResourceType parseResourceType(String type) {
        try {
            return ResourceType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid resourceType '" + type + "'. Allowed values: Labor, Equipment");
        }
    }

    private ResourceRequestResponse toResponse(ResourceRequestEntity e) {
        return new ResourceRequestResponse(
                e.getRequestId(),
                e.getProjectId(),
                e.getRequestedBy(),
                e.getLogId(),
                e.getResourceType(),
                e.getResourceId(),
                e.getRequirementDescription(),
                e.getRequiredFrom(),
                e.getRequiredUntil(),
                e.getStatus(),
                e.getRejectionReason(),
                e.getAllocationId(),
                e.getRequestedAt(),
                e.getActionedAt(),
                e.getActionedBy()
        );
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }

    // ── User identity validation helpers ────────────────────────────────────

    /**
     * Validates that the given userId is a real, ACTIVE SITE_ENGINEER (or ADMIN).
     */
    private void validateSiteEngineerUser(String userId, String fieldName) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "'" + fieldName + "' (Site Engineer user ID) is required.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: '" + userId + "' in field '" + fieldName + "'. "
                        + "This user ID does not exist. "
                        + "Check GET /api/v1/admin/users for valid IDs."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is "
                    + user.getStatus() + " and cannot perform this action. "
                    + "Only ACTIVE users can submit or cancel resource requests.");
        }
        if (user.getRole() != Role.SITE_ENGINEER && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") has role "
                    + user.getRole() + ". Only a SITE_ENGINEER can submit or cancel "
                    + "resource requests. If you are a Site Engineer, use your own user ID.");
        }
    }

    /**
     * Validates that the given userId is a real, ACTIVE PROJECT_MANAGER (or ADMIN).
     */
    private void validateProjectManagerUser(String userId, String fieldName) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "'" + fieldName + "' (Project Manager user ID) is required.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: '" + userId + "' in field '" + fieldName + "'. "
                        + "This user ID does not exist. "
                        + "Check GET /api/v1/admin/users for valid IDs."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") is "
                    + user.getStatus() + " and cannot perform this action. "
                    + "Only ACTIVE users can approve, reject, or release resource requests.");
        }
        if (user.getRole() != Role.PROJECT_MANAGER && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "User '" + userId + "' (" + user.getName() + ") has role "
                    + user.getRole() + ". Only a PROJECT_MANAGER can approve, reject, "
                    + "or release resource requests. Site Engineers cannot approve their own requests.");
        }
    }
}
