package com.buildsmart.siteops.controller;

import com.buildsmart.siteops.dto.IssueRequest;
import com.buildsmart.siteops.dto.IssueResponse;
import com.buildsmart.siteops.dto.IssueUpdateRequest;
import com.buildsmart.siteops.enums.IssueSeverity;
import com.buildsmart.siteops.enums.IssueStatus;
import com.buildsmart.siteops.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Site issue reporting and tracking. CRITICAL issues auto-escalate to Project Manager.")
public class IssueController {

    private final IssueService issueService;

    @Operation(summary = "Report a new site issue. CRITICAL severity auto-escalates and notifies PM immediately.")
    @PostMapping
    public ResponseEntity<IssueResponse> create(@Valid @RequestBody IssueRequest request) {
        IssueResponse created = issueService.createIssue(request);
        return ResponseEntity
                .created(URI.create("/api/v1/issues/" + created.issueId()))
                .body(created);
    }

    @Operation(summary = "Get an issue by ID")
    @GetMapping("/{issueId}")
    public ResponseEntity<IssueResponse> getById(@PathVariable String issueId) {
        return ResponseEntity.ok(issueService.getIssueById(issueId));
    }

    @Operation(summary = "List issues for a project, optionally filtered by status, severity, or reporter")
    @GetMapping
    public ResponseEntity<List<IssueResponse>> list(
            @RequestParam String projectId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssueSeverity severity,
            @RequestParam(required = false) String reportedBy) {

        List<IssueResponse> result;
        if (status != null) {
            result = issueService.getIssuesByProjectAndStatus(projectId, status);
        } else if (severity != null) {
            result = issueService.getIssuesByProjectAndSeverity(projectId, severity);
        } else if (reportedBy != null) {
            result = issueService.getIssuesByProjectAndReporter(projectId, reportedBy);
        } else {
            result = issueService.getIssuesByProject(projectId);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "List all issues linked to a specific site log")
    @GetMapping("/by-log/{logId}")
    public ResponseEntity<List<IssueResponse>> byLog(@PathVariable String logId) {
        return ResponseEntity.ok(issueService.getIssuesByLogId(logId));
    }

    @Operation(summary = "Update issue description, status, assignee, or resolution notes")
    @PatchMapping("/{issueId}")
    public ResponseEntity<IssueResponse> update(
            @PathVariable String issueId,
            @Valid @RequestBody IssueUpdateRequest request) {
        return ResponseEntity.ok(issueService.updateIssue(issueId, request));
    }
}
