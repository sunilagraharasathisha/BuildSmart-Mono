package com.buildsmart.projectmanager.controller;

import com.buildsmart.projectmanager.dto.ProjectRequest;
import com.buildsmart.projectmanager.dto.ProjectResponse;
import com.buildsmart.projectmanager.service.ProjectService;
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
@RequestMapping("/api/project-manager/projects")
@RequiredArgsConstructor
@Tag(name = "Project Manager APIs", description = "Project management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER','SITE_ENGINEER')")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create project")
    @ApiResponse(responseCode = "201", description = "Project created", content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by ID")
    @ApiResponse(responseCode = "200", description = "Project fetched")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @GetMapping
    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", description = "Projects fetched")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update project")
    @ApiResponse(responseCode = "200", description = "Project updated", content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String projectId,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(projectId, request));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete project")
    @ApiResponse(responseCode = "204", description = "Project deleted")
    public ResponseEntity<Void> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/expenses/{expenseId}/approve")
    @Operation(summary = "Project manager approves expense")
    @ApiResponse(responseCode = "200", description = "Expense approved")
    public ResponseEntity<com.buildsmart.finance.dto.ExpenseResponse> approveExpense(
            @PathVariable String expenseId,
            @RequestParam String approvedBy) {
        return ResponseEntity.ok(projectService.approveExpense(expenseId, approvedBy));
    }

    @PostMapping("/expenses/{expenseId}/reject")
    @Operation(summary = "Project manager rejects expense")
    @ApiResponse(responseCode = "200", description = "Expense rejected")
    public ResponseEntity<com.buildsmart.finance.dto.ExpenseResponse> rejectExpense(
            @PathVariable String expenseId,
            @RequestParam String approvedBy) {
        return ResponseEntity.ok(projectService.rejectExpense(expenseId, approvedBy));
    }
}
