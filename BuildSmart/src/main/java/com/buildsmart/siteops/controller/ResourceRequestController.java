package com.buildsmart.siteops.controller;

import com.buildsmart.siteops.dto.ResourceRequestApprovalRequest;
import com.buildsmart.siteops.dto.ResourceRequestCreateRequest;
import com.buildsmart.siteops.dto.ResourceRequestRejectionRequest;
import com.buildsmart.siteops.dto.ResourceRequestResponse;
import com.buildsmart.siteops.service.ResourceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Resource Request endpoints.
 *
 * Typical Swagger test flow:
 * ───────────────────────────
 * 1. POST /api/v1/resource-requests          — SE submits request (status = PENDING)
 * 2. GET  /api/v1/resource-requests/project/{projectId}/pending  — PM sees what needs attention
 * 3. GET  /api/v1/resources?availability=Available              — PM checks available resources
 * 4. POST /api/v1/resource-requests/{id}/approve                — PM picks resource → APPROVED, Allocation created
 *    OR
 *    POST /api/v1/resource-requests/{id}/reject                 — PM rejects with reason
 * 5. POST /api/v1/resource-requests/{id}/release                — PM releases when work done
 *    OR
 *    POST /api/v1/resource-requests/{id}/cancel                 — SE cancels pending request
 */
@RestController
@RequestMapping("/api/resource-requests")
@RequiredArgsConstructor
@Tag(
    name = "Resource Requests",
    description = "Construction site resource request workflow. " +
                  "Site Engineer requests Labor/Equipment → Project Manager approves and assigns a specific resource → " +
                  "System auto-creates an Active Allocation. " +
                  "Workflow: PENDING → APPROVED / REJECTED / CANCELLED → RELEASED"
)
public class ResourceRequestController {

    private final ResourceRequestService resourceRequestService;

    /* ─── CREATE ─────────────────────────────────────────────── */

    @Operation(
        summary = "Submit a resource request (Site Engineer)",
        description = "Site Engineer raises a request for Labor or Equipment. " +
                      "The system notifies the Project Manager. Status starts as PENDING. " +
                      "Tip: link to a SiteLog by providing logId to give PM full context."
    )
    @PostMapping
    public ResponseEntity<ResourceRequestResponse> create(
            @Valid @RequestBody ResourceRequestCreateRequest request) {
        ResourceRequestResponse created = resourceRequestService.createRequest(request);
        return ResponseEntity
                .created(URI.create("/api/v1/resource-requests/" + created.requestId()))
                .body(created);
    }

    /* ─── READ ───────────────────────────────────────────────── */

    @Operation(summary = "Get a resource request by ID")
    @GetMapping("/{requestId}")
    public ResponseEntity<ResourceRequestResponse> getById(@PathVariable String requestId) {
        return ResponseEntity.ok(resourceRequestService.getRequestById(requestId));
    }

    @Operation(
        summary = "List all resource requests for a project (PM's full view)",
        description = "Returns all requests (PENDING, APPROVED, REJECTED, RELEASED, CANCELLED) for a project, newest first."
    )
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceRequestResponse>> byProject(
            @PathVariable String projectId) {
        return ResponseEntity.ok(resourceRequestService.getRequestsByProject(projectId));
    }

    @Operation(
        summary = "List PENDING resource requests for a project (PM's action queue)",
        description = "Returns only PENDING requests sorted oldest-first so PM handles them in order."
    )
    @GetMapping("/project/{projectId}/pending")
    public ResponseEntity<List<ResourceRequestResponse>> pendingByProject(
            @PathVariable String projectId) {
        return ResponseEntity.ok(resourceRequestService.getPendingRequestsByProject(projectId));
    }

    @Operation(
        summary = "List all resource requests raised by a Site Engineer",
        description = "Site Engineer can track the status of all their requests across projects."
    )
    @GetMapping("/by-engineer/{requestedBy}")
    public ResponseEntity<List<ResourceRequestResponse>> byEngineer(
            @PathVariable String requestedBy) {
        return ResponseEntity.ok(resourceRequestService.getRequestsByEngineer(requestedBy));
    }

    @Operation(
        summary = "List resource requests linked to a specific site log",
        description = "Useful to see what resources were requested when reviewing a daily log."
    )
    @GetMapping("/by-log/{logId}")
    public ResponseEntity<List<ResourceRequestResponse>> byLog(@PathVariable String logId) {
        return ResponseEntity.ok(resourceRequestService.getRequestsByLog(logId));
    }

    /* ─── APPROVE ────────────────────────────────────────────── */

    @Operation(
        summary = "Approve a resource request (Project Manager)",
        description = "PM approves the request and assigns a specific resource by ID. " +
                      "The system automatically creates an Active Allocation in the Resource module " +
                      "and marks the resource as Allocated. Site Engineer is notified. " +
                      "Use GET /api/v1/resources to browse available resources before approving."
    )
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<ResourceRequestResponse> approve(
            @PathVariable String requestId,
            @Valid @RequestBody ResourceRequestApprovalRequest request) {
        return ResponseEntity.ok(resourceRequestService.approveRequest(requestId, request));
    }

    /* ─── REJECT ─────────────────────────────────────────────── */

    @Operation(
        summary = "Reject a resource request (Project Manager)",
        description = "PM rejects the request with a mandatory reason. " +
                      "Site Engineer is notified with the reason so they can revise and resubmit."
    )
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ResourceRequestResponse> reject(
            @PathVariable String requestId,
            @Valid @RequestBody ResourceRequestRejectionRequest request) {
        return ResponseEntity.ok(resourceRequestService.rejectRequest(requestId, request));
    }

    /* ─── RELEASE ────────────────────────────────────────────── */

    @Operation(
        summary = "Release an approved resource request (Project Manager)",
        description = "PM marks the work as done. The underlying Allocation transitions to Released " +
                      "and the resource's availability is reset to Available for other projects. " +
                      "Site Engineer is notified."
    )
    @PostMapping("/{requestId}/release")
    public ResponseEntity<ResourceRequestResponse> release(
            @PathVariable String requestId,
            @Parameter(description = "PM's user ID performing the release")
            @RequestParam String actionedBy) {
        return ResponseEntity.ok(resourceRequestService.releaseRequest(requestId, actionedBy));
    }

    /* ─── CANCEL ─────────────────────────────────────────────── */

    @Operation(
        summary = "Cancel a pending resource request (Site Engineer)",
        description = "Site Engineer can cancel their own PENDING request before the PM acts on it. " +
                      "Once APPROVED, only the PM can release the resource."
    )
    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<ResourceRequestResponse> cancel(
            @PathVariable String requestId,
            @Parameter(description = "Site Engineer's user ID cancelling the request")
            @RequestParam String cancelledBy) {
        return ResponseEntity.ok(resourceRequestService.cancelRequest(requestId, cancelledBy));
    }
}
