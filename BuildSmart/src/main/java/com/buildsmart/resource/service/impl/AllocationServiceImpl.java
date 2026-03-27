package com.buildsmart.resource.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.NotificationService;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.resource.dto.AllocationRequest;
import com.buildsmart.resource.dto.AllocationResponse;
import com.buildsmart.resource.dto.AllocationStatusChangeRequest;
import com.buildsmart.resource.dto.AllocationUpdateDatesRequest;
import com.buildsmart.resource.entity.AllocationEntity;
import com.buildsmart.resource.entity.ResourceEntity;
import com.buildsmart.resource.enums.AllocationStatus;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.mapper.AllocationMapper;
import com.buildsmart.resource.repository.AllocationRepository;
import com.buildsmart.resource.repository.ResourceRepository;
import com.buildsmart.resource.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Core allocation service.
 *
 * Construction scenario:
 *  1. Site Engineer creates a SiteLog and requests resources (Labor/Equipment).
 *  2. A Planned allocation is created → PM is notified.
 *  3. PM reviews and activates the allocation → Site Engineer is notified.
 *  4. When work is done, PM releases the allocation → resource becomes Available again.
 *
 * Business rules (preserved from friend's module):
 *  - Date range validation (assignedDate <= releasedDate).
 *  - No two Planned/Active allocations may overlap for the same resource.
 *  - Unavailable / Maintenance resources cannot be activated.
 *  - Allowed status transitions: Planned→Active, Planned→Cancelled, Active→Released, Active→Cancelled.
 *  - Terminal states (Released, Cancelled) are immutable.
 *  - Optional project headcount cap (set via property).
 *  - Resource availability is auto-refreshed after every status change.
 */
@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    /** Far-future sentinel used for open-ended allocations in JPQL overlap queries. */
    private static final LocalDate FAR_FUTURE = LocalDate.of(9999, 12, 31);

    private final AllocationRepository allocationRepository;
    private final ResourceRepository   resourceRepository;
    private final ProjectRepository    projectRepository;
    private final NotificationService  notificationService;

    /**
     * Maximum number of Active allocations per project on any given day.
     * 0 = unlimited (default).
     */
    @Value("${buildsmart.allocations.project.max-active:0}")
    private int maxActivePerProject;

    /* ════════════════════════════════════════════════════════════
       CREATE
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public AllocationResponse createAllocation(AllocationRequest request) {

        // ── A1: Date validation ──────────────────────────────────────────────
        LocalDate start = request.assignedDate();
        LocalDate end   = request.releasedDate();
        if (start == null) {
            throw new IllegalArgumentException("assignedDate is required");
        }
        if (end != null && end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "releasedDate must be on or after assignedDate");
        }

        // ── A2: Status parse ─────────────────────────────────────────────────
        AllocationStatus status = parseStatus(request.status());

        // ── A3: Project existence check ──────────────────────────────────────
        if (!projectRepository.existsById(request.projectId())) {
            throw new ResourceNotFoundException(
                    "Project not found: " + request.projectId());
        }

        // ── A4: Resource existence check ─────────────────────────────────────
        ResourceEntity resource = findResource(request.resourceId());

        // ── A5: Availability gate for Active status ──────────────────────────
        if (status == AllocationStatus.Active) {
            ensureResourceCanActivate(resource);
        }

        // ── B1: Overlap prevention ────────────────────────────────────────────
        long overlapping = allocationRepository.countOverlappingForResource(
                resource.getResourceId(), start, end,
                List.of(AllocationStatus.Planned, AllocationStatus.Active),
                FAR_FUTURE);
        if (overlapping > 0) {
            throw new DuplicateResourceException(overlapMsg(resource, start, end));
        }

        // ── B2: Project headcount cap ─────────────────────────────────────────
        if (status == AllocationStatus.Active && maxActivePerProject > 0) {
            long activeOnStart = allocationRepository.countActiveOnDateForProject(
                    request.projectId(), start, AllocationStatus.Active);
            if (activeOnStart >= maxActivePerProject) {
                throw new IllegalArgumentException(
                        "Project " + request.projectId() + " has reached the Active resource cap of "
                        + maxActivePerProject + " on " + start);
            }
        }

        // ── Persist ───────────────────────────────────────────────────────────
        String lastId = allocationRepository.findTopByOrderByAllocationIdDesc()
                .map(AllocationEntity::getAllocationId)
                .orElse(null);

        AllocationEntity allocation = new AllocationEntity();
        allocation.setAllocationId(IdGeneratorUtil.nextAllocationId(lastId));
        allocation.setProjectId(request.projectId());
        allocation.setResource(resource);
        allocation.setAssignedDate(start);
        allocation.setReleasedDate(end);
        allocation.setStatus(status);
        allocation.setRequestNotes(request.requestNotes());

        AllocationEntity saved = allocationRepository.save(allocation);

        // ── Auto-refresh resource availability ───────────────────────────────
        if (status == AllocationStatus.Active) {
            refreshResourceAvailability(resource);
        }

        // ── Notify Project Manager that a resource was requested ─────────────
        // The PM's userId is tracked via IAM; we use a fixed routing key here.
        // In a full system this would look up the assigned PM for the project.
        notificationService.createNotification(
                "PM-" + saved.getProjectId(),   // routed to PM of this project
                saved.getAllocationId(),
                saved.getProjectId(),
                String.format("Resource request [%s] '%s' (%s) needs your approval for project %s (status: %s).",
                        saved.getAllocationId(),
                        resource.getResourceName(),
                        resource.getType(),
                        saved.getProjectId(),
                        saved.getStatus())
        );

        return AllocationMapper.toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       READ
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional(readOnly = true)
    public AllocationResponse getAllocationById(String allocationId) {
        return AllocationMapper.toResponse(findAllocation(allocationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponse> getAllocationsByProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return allocationRepository.findByProjectId(projectId)
                .stream().map(AllocationMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponse> getAllocationsByResource(String resourceId) {
        findResource(resourceId); // validate existence
        return allocationRepository.findByResourceResourceId(resourceId)
                .stream().map(AllocationMapper::toResponse).toList();
    }

    /* ════════════════════════════════════════════════════════════
       UPDATE DATES
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public AllocationResponse updateDates(String allocationId, AllocationUpdateDatesRequest request) {
        AllocationEntity a = findAllocation(allocationId);

        if (a.getStatus() == AllocationStatus.Released || a.getStatus() == AllocationStatus.Cancelled) {
            throw new IllegalArgumentException(
                    "Cannot change dates for a " + a.getStatus() + " allocation");
        }

        LocalDate newStart = request.assignedDate();
        LocalDate newEnd   = request.releasedDate();
        if (newStart == null) throw new IllegalArgumentException("assignedDate is required");
        if (newEnd != null && newEnd.isBefore(newStart)) {
            throw new IllegalArgumentException("releasedDate must be on or after assignedDate");
        }

        long overlapping = allocationRepository.countOverlappingExcluding(
                a.getResource().getResourceId(), newStart, newEnd,
                List.of(AllocationStatus.Planned, AllocationStatus.Active),
                FAR_FUTURE, a.getAllocationId());
        if (overlapping > 0) {
            throw new DuplicateResourceException(overlapMsg(a.getResource(), newStart, newEnd));
        }

        if (a.getStatus() == AllocationStatus.Active && maxActivePerProject > 0) {
            long activeOnStart = allocationRepository.countActiveOnDateForProject(
                    a.getProjectId(), newStart, AllocationStatus.Active);
            if (activeOnStart > maxActivePerProject) {
                throw new IllegalArgumentException(
                        "Updating dates would exceed project cap of " + maxActivePerProject
                        + " Active allocations on " + newStart);
            }
        }

        a.setAssignedDate(newStart);
        a.setReleasedDate(newEnd);
        AllocationEntity saved = allocationRepository.save(a);

        if (a.getStatus() == AllocationStatus.Active) {
            refreshResourceAvailability(a.getResource());
        }

        return AllocationMapper.toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       CHANGE STATUS
       ════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public AllocationResponse changeStatus(String allocationId, AllocationStatusChangeRequest request) {
        AllocationEntity a      = findAllocation(allocationId);
        AllocationStatus newSt  = parseStatus(request.status());
        AllocationStatus current = a.getStatus();

        validateTransition(current, newSt);

        if (newSt == AllocationStatus.Active) {
            ensureResourceCanActivate(a.getResource());

            long overlapping = allocationRepository.countOverlappingExcluding(
                    a.getResource().getResourceId(),
                    a.getAssignedDate(), a.getReleasedDate(),
                    List.of(AllocationStatus.Planned, AllocationStatus.Active),
                    FAR_FUTURE, a.getAllocationId());
            if (overlapping > 0) {
                throw new DuplicateResourceException(
                        overlapMsg(a.getResource(), a.getAssignedDate(), a.getReleasedDate()));
            }

            if (maxActivePerProject > 0) {
                long activeOnStart = allocationRepository.countActiveOnDateForProject(
                        a.getProjectId(), a.getAssignedDate(), AllocationStatus.Active);
                if (activeOnStart >= maxActivePerProject) {
                    throw new IllegalArgumentException(
                            "Project " + a.getProjectId() + " reached Active resource cap of "
                            + maxActivePerProject + " on " + a.getAssignedDate());
                }
            }
        }

        if (newSt == AllocationStatus.Released) {
            if (a.getReleasedDate() == null) {
                a.setReleasedDate(LocalDate.now());
            }
            if (a.getReleasedDate().isBefore(a.getAssignedDate())) {
                throw new IllegalArgumentException(
                        "Cannot release: releasedDate is before assignedDate");
            }
        }

        a.setStatus(newSt);
        AllocationEntity saved = allocationRepository.save(a);
        refreshResourceAvailability(a.getResource());

        // ── Notify Site Engineer when PM approves (Planned → Active) ─────────
        if (newSt == AllocationStatus.Active) {
            notificationService.createNotification(
                    "SE-" + saved.getProjectId(),    // routed to site engineers on project
                    saved.getAllocationId(),
                    saved.getProjectId(),
                    String.format("Resource '%s' (%s) has been APPROVED and is now Active for project %s. Allocation: %s",
                            a.getResource().getResourceName(),
                            a.getResource().getType(),
                            saved.getProjectId(),
                            saved.getAllocationId())
            );
        }

        // ── Notify Site Engineer when resource is Released ────────────────────
        if (newSt == AllocationStatus.Released) {
            notificationService.createNotification(
                    "SE-" + saved.getProjectId(),
                    saved.getAllocationId(),
                    saved.getProjectId(),
                    String.format("Resource '%s' has been RELEASED from project %s. Allocation: %s",
                            a.getResource().getResourceName(),
                            saved.getProjectId(),
                            saved.getAllocationId())
            );
        }

        // ── Notify PM when SE cancels a pending request ──────────────────────
        if (newSt == AllocationStatus.Cancelled) {
            notificationService.createNotification(
                    "PM-" + saved.getProjectId(),
                    saved.getAllocationId(),
                    saved.getProjectId(),
                    String.format("Resource request %s for '%s' on project %s has been CANCELLED.",
                            saved.getAllocationId(),
                            a.getResource().getResourceName(),
                            saved.getProjectId())
            );
        }

        return AllocationMapper.toResponse(saved);
    }

    /* ════════════════════════════════════════════════════════════
       PRIVATE HELPERS
       ════════════════════════════════════════════════════════════ */

    private AllocationEntity findAllocation(String allocationId) {
        return allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Allocation not found: " + allocationId));
    }

    private ResourceEntity findResource(String resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found: " + resourceId));
    }

    private AllocationStatus parseStatus(String statusText) {
        try {
            return AllocationStatus.valueOf(statusText);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid status '" + statusText + "'. Allowed: Planned, Active, Released, Cancelled");
        }
    }

    private void validateTransition(AllocationStatus current, AllocationStatus target) {
        boolean allowed = switch (current) {
            case Planned  -> target == AllocationStatus.Active || target == AllocationStatus.Cancelled;
            case Active   -> target == AllocationStatus.Released || target == AllocationStatus.Cancelled;
            case Released, Cancelled -> false;
        };
        if (!allowed) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + current + " → " + target
                    + ". Allowed: Planned→Active/Cancelled, Active→Released/Cancelled");
        }
    }

    private void ensureResourceCanActivate(ResourceEntity resource) {
        if (resource.getAvailability() == ResourceAvailability.Unavailable
                || resource.getAvailability() == ResourceAvailability.Maintenance) {
            throw new IllegalArgumentException(
                    "Resource '" + resource.getResourceName() + "' (ID: " + resource.getResourceId()
                    + ") is currently " + resource.getAvailability()
                    + " and cannot be activated. Please select an Available or Allocated resource.");
        }
    }

    /**
     * Auto-syncs Resource.availability based on whether there are any Active
     * allocations today. Respects Unavailable/Maintenance states (admin-managed).
     */
    private void refreshResourceAvailability(ResourceEntity resource) {
        long activeNow = allocationRepository.countActiveOnDateForResource(
                resource.getResourceId(), LocalDate.now(), AllocationStatus.Active);

        switch (resource.getAvailability()) {
            case Maintenance, Unavailable -> { /* admin-managed; do not override */ }
            case Allocated -> {
                if (activeNow == 0) {
                    resource.setAvailability(ResourceAvailability.Available);
                    resourceRepository.save(resource);
                }
            }
            case Available -> {
                if (activeNow > 0) {
                    resource.setAvailability(ResourceAvailability.Allocated);
                    resourceRepository.save(resource);
                }
            }
        }
    }

    private String overlapMsg(ResourceEntity resource, LocalDate start, LocalDate end) {
        String endStr = (end == null ? "open-ended" : end.toString());
        return "Resource '" + resource.getResourceName() + "' (ID: " + resource.getResourceId()
               + ") already has a Planned/Active allocation overlapping ["
               + start + " .. " + endStr + "]. Please choose a different date range or resource.";
    }
}
