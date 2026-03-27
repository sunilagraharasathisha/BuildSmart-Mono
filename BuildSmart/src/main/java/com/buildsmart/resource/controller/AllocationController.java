package com.buildsmart.resource.controller;

import com.buildsmart.resource.dto.AllocationRequest;
import com.buildsmart.resource.dto.AllocationResponse;
import com.buildsmart.resource.dto.AllocationStatusChangeRequest;
import com.buildsmart.resource.dto.AllocationUpdateDatesRequest;
import com.buildsmart.resource.service.AllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
@Tag(name = "Allocations", description = "Resource allocation workflow: Site Engineer requests → PM approves/releases")
public class AllocationController {

    private final AllocationService allocationService;

    @Operation(summary = "Request a resource allocation (Site Engineer raises request for Labor/Equipment)")
    @PostMapping
    public ResponseEntity<AllocationResponse> create(@Valid @RequestBody AllocationRequest request) {
        AllocationResponse created = allocationService.createAllocation(request);
        return ResponseEntity
                .created(URI.create("/api/v1/allocations/" + created.allocationId()))
                .body(created);
    }

    @Operation(summary = "Get allocation by ID")
    @GetMapping("/{allocationId}")
    public ResponseEntity<AllocationResponse> getById(@PathVariable String allocationId) {
        return ResponseEntity.ok(allocationService.getAllocationById(allocationId));
    }

    @Operation(summary = "List all allocations for a project")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<AllocationResponse>> byProject(@PathVariable String projectId) {
        return ResponseEntity.ok(allocationService.getAllocationsByProject(projectId));
    }

    @Operation(summary = "List all allocations for a resource")
    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<AllocationResponse>> byResource(@PathVariable String resourceId) {
        return ResponseEntity.ok(allocationService.getAllocationsByResource(resourceId));
    }

    @Operation(summary = "Update allocation dates (Planned or Active only)")
    @PutMapping("/{allocationId}/dates")
    public ResponseEntity<AllocationResponse> updateDates(
            @PathVariable String allocationId,
            @Valid @RequestBody AllocationUpdateDatesRequest request) {
        return ResponseEntity.ok(allocationService.updateDates(allocationId, request));
    }

    @Operation(summary = "Change allocation status. PM uses this to Activate (approve) or Release. " +
                         "Transitions: Planned→Active, Planned→Cancelled, Active→Released, Active→Cancelled")
    @PatchMapping("/{allocationId}/status")
    public ResponseEntity<AllocationResponse> changeStatus(
            @PathVariable String allocationId,
            @Valid @RequestBody AllocationStatusChangeRequest request) {
        return ResponseEntity.ok(allocationService.changeStatus(allocationId, request));
    }
}
