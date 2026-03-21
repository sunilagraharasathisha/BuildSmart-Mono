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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-manager/projects")
@RequiredArgsConstructor
@Tag(name = "Project Manager APIs", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create project")
    @ApiResponse(responseCode = "201", description = "Project created",
            content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
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
}
