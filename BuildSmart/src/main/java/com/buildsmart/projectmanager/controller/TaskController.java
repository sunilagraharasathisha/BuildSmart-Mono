package com.buildsmart.projectmanager.controller;

import com.buildsmart.projectmanager.dto.TaskRequest;
import com.buildsmart.projectmanager.dto.TaskResponse;
import com.buildsmart.projectmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/project-manager/tasks")
@RequiredArgsConstructor
@Tag(name = "Project Manager APIs", description = "Task management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER','SITE_ENGINEER')")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task")
    @ApiResponse(responseCode = "201", description = "Task created")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project ID")
    @ApiResponse(responseCode = "200", description = "Tasks fetched")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task")
    @ApiResponse(responseCode = "200", description = "Task updated")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String taskId, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
