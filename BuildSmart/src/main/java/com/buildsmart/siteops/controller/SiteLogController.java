package com.buildsmart.siteops.controller;

import com.buildsmart.siteops.dto.SiteLogRequest;
import com.buildsmart.siteops.dto.SiteLogResponse;
import com.buildsmart.siteops.dto.SiteLogReviewRequest;
import com.buildsmart.siteops.dto.SiteLogUpdateRequest;
import com.buildsmart.siteops.service.SiteLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sitelogs")
@RequiredArgsConstructor
@Tag(name = "Site Logs", description = "Daily construction site log management for Site Engineers")
public class SiteLogController {

    private final SiteLogService siteLogService;

    @Operation(summary = "Submit today's site log (Site Engineer). One log per project per day.")
    @PostMapping
    public ResponseEntity<SiteLogResponse> create(@Valid @RequestBody SiteLogRequest request) {
        SiteLogResponse created = siteLogService.createSiteLog(request);
        return ResponseEntity
                .created(URI.create("/api/v1/sitelogs/" + created.logId()))
                .body(created);
    }

    @Operation(summary = "Get a site log by its ID")
    @GetMapping("/{logId}")
    public ResponseEntity<SiteLogResponse> getById(@PathVariable String logId) {
        return ResponseEntity.ok(siteLogService.getSiteLogById(logId));
    }

    @Operation(summary = "List site logs for a project, optionally filtered by date range")
    @GetMapping
    public ResponseEntity<List<SiteLogResponse>> list(
            @RequestParam String projectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<SiteLogResponse> result = (from != null && to != null)
                ? siteLogService.getSiteLogsByProjectAndDateRange(projectId, from, to)
                : siteLogService.getSiteLogsByProject(projectId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get the site log for a specific project and date")
    @GetMapping("/by-date")
    public ResponseEntity<SiteLogResponse> byDate(
            @RequestParam String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(siteLogService.getSiteLogByProjectAndDate(projectId, date));
    }

    @Operation(summary = "Get the most recent site log for a project")
    @GetMapping("/latest/{projectId}")
    public ResponseEntity<SiteLogResponse> latest(@PathVariable String projectId) {
        return ResponseEntity.ok(siteLogService.getLatestSiteLog(projectId));
    }

    @Operation(summary = "Update activities, issues summary, or progress percent of a site log")
    @PatchMapping("/{logId}")
    public ResponseEntity<SiteLogResponse> update(
            @PathVariable String logId,
            @Valid @RequestBody SiteLogUpdateRequest request) {
        return ResponseEntity.ok(siteLogService.updateSiteLog(logId, request));
    }

    @Operation(summary = "Project Manager reviews and comments on a site log")
    @PostMapping("/{logId}/review")
    public ResponseEntity<SiteLogResponse> review(
            @PathVariable String logId,
            @Valid @RequestBody SiteLogReviewRequest request) {
        return ResponseEntity.ok(siteLogService.reviewSiteLog(logId, request));
    }
}
