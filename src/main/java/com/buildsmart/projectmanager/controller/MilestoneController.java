package com.buildsmart.projectmanager.controller;

import com.buildsmart.projectmanager.dto.MilestoneRequest;
import com.buildsmart.projectmanager.dto.MilestoneResponse;
import com.buildsmart.projectmanager.service.MilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-manager/milestones")
@RequiredArgsConstructor
@Tag(name = "Project Manager APIs", description = "Milestone management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER','SITE_ENGINEER')")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    @Operation(summary = "Create milestone")
    @ApiResponse(responseCode = "201", description = "Milestone created",
            content = @Content(schema = @Schema(implementation = MilestoneResponse.class)))
    public ResponseEntity<MilestoneResponse> createMilestone(@Valid @RequestBody MilestoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(milestoneService.createMilestone(request));
    }

    @GetMapping("/{milestoneId}")
    @Operation(summary = "Get milestone by ID")
    @ApiResponse(responseCode = "200", description = "Milestone fetched")
    public ResponseEntity<MilestoneResponse> getMilestone(@PathVariable String milestoneId) {
        return ResponseEntity.ok(milestoneService.getMilestoneById(milestoneId));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get milestones by project ID")
    @ApiResponse(responseCode = "200", description = "Milestones fetched")
    public ResponseEntity<List<MilestoneResponse>> getMilestonesByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(milestoneService.getMilestonesByProjectId(projectId));
    }

    @PutMapping("/{milestoneId}")
    @Operation(summary = "Update milestone")
    @ApiResponse(responseCode = "200", description = "Milestone updated")
    public ResponseEntity<MilestoneResponse> updateMilestone(@PathVariable String milestoneId,
                                                             @Valid @RequestBody MilestoneRequest request) {
        return ResponseEntity.ok(milestoneService.updateMilestone(milestoneId, request));
    }

    @DeleteMapping("/{milestoneId}")
    @Operation(summary = "Delete milestone")
    @ApiResponse(responseCode = "204", description = "Milestone deleted")
    public ResponseEntity<Void> deleteMilestone(@PathVariable String milestoneId) {
        milestoneService.deleteMilestone(milestoneId);
        return ResponseEntity.noContent().build();
    }
}