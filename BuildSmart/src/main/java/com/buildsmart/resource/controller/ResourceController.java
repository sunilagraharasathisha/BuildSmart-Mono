package com.buildsmart.resource.controller;

import com.buildsmart.resource.dto.ResourceRequest;
import com.buildsmart.resource.dto.ResourceResponse;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;
import com.buildsmart.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Manage Labor and Equipment resources for construction projects")
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "Register a new resource (Labor or Equipment)")
    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest request) {
        ResourceResponse created = resourceService.createResource(request);
        return ResponseEntity
                .created(URI.create("/api/v1/resources/" + created.resourceId()))
                .body(created);
    }

    @Operation(summary = "Get a resource by ID")
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getById(@PathVariable String resourceId) {
        return ResponseEntity.ok(resourceService.getResourceById(resourceId));
    }

    @Operation(summary = "List all resources, optionally filtered by type")
    @GetMapping
    public ResponseEntity<List<ResourceResponse>> list(
            @RequestParam(required = false) ResourceType type) {
        List<ResourceResponse> result = (type != null)
                ? resourceService.getResourcesByType(type)
                : resourceService.getAllResources();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "List resources that are currently Available, optionally filtered by type")
    @GetMapping("/available")
    public ResponseEntity<List<ResourceResponse>> available(
            @RequestParam(required = false) ResourceType type) {
        return ResponseEntity.ok(resourceService.getAvailableResources(type));
    }

    @Operation(summary = "Manually update a resource's availability (admin/PM use)")
    @PatchMapping("/{resourceId}/availability")
    public ResponseEntity<ResourceResponse> updateAvailability(
            @PathVariable String resourceId,
            @RequestParam ResourceAvailability availability) {
        return ResponseEntity.ok(resourceService.updateAvailability(resourceId, availability));
    }
}
