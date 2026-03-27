package com.buildsmart.safety.web.controller;

import com.buildsmart.safety.domain.model.IncidentSeverity;
import com.buildsmart.safety.domain.model.IncidentStatus;
import com.buildsmart.safety.service.IncidentService;
import com.buildsmart.safety.web.dto.IncidentDtos.CreateIncidentRequest;
import com.buildsmart.safety.web.dto.IncidentDtos.IncidentResponse;
import com.buildsmart.safety.web.dto.IncidentDtos.UpdateIncidentStatusRequest;
import com.buildsmart.safety.web.dto.SafetyPageResponse;
import com.buildsmart.safety.web.mapper.IncidentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/safety/incidents")
@RequiredArgsConstructor
@Tag(name = "Safety - Incidents", description = "Incident reporting and management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'SAFETY_OFFICER', 'SITE_ENGINEER')")
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    @Operation(summary = "Report a new incident")
    @ApiResponse(responseCode = "201", description = "Incident reported successfully",
            content = @Content(schema = @Schema(implementation = IncidentResponse.class)))
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IncidentMapper.toResponse(incidentService.create(request)));
    }

    @GetMapping("/{incidentId}")
    @Operation(summary = "Get incident by ID")
    @ApiResponse(responseCode = "200", description = "Incident fetched")
    @ApiResponse(responseCode = "404", description = "Incident not found")
    public ResponseEntity<IncidentResponse> get(@PathVariable String incidentId) {
        return ResponseEntity.ok(IncidentMapper.toResponse(incidentService.get(incidentId)));
    }

    @GetMapping
    @Operation(summary = "Search incidents with optional filters and pagination")
    @ApiResponse(responseCode = "200", description = "Incidents fetched")
    public ResponseEntity<SafetyPageResponse<IncidentResponse>> search(
            @RequestParam Optional<String> projectId,
            @RequestParam Optional<IncidentStatus> status,
            @RequestParam Optional<IncidentSeverity> severity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date,desc") String sort
    ) {
        Sort sortObj = Sort.by(sort.split(",")[0]);
        if (sort.endsWith(",desc")) sortObj = sortObj.descending();

        Page<IncidentResponse> result = incidentService
                .search(projectId, status, severity, dateFrom, dateTo, PageRequest.of(page, size, sortObj))
                .map(IncidentMapper::toResponse);

        return ResponseEntity.ok(new SafetyPageResponse<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        ));
    }

    @PatchMapping("/{incidentId}/status")
    @Operation(summary = "Update incident status")
    @ApiResponse(responseCode = "200", description = "Status updated")
    @ApiResponse(responseCode = "404", description = "Incident not found")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable String incidentId,
            @Valid @RequestBody UpdateIncidentStatusRequest request) {
        return ResponseEntity.ok(
                IncidentMapper.toResponse(incidentService.updateStatus(incidentId, request.status()))
        );
    }

    @DeleteMapping("/{incidentId}")
    @Operation(summary = "Delete an incident")
    @ApiResponse(responseCode = "204", description = "Incident deleted")
    @ApiResponse(responseCode = "404", description = "Incident not found")
    @PreAuthorize("hasAnyRole('ADMIN', 'SAFETY_OFFICER')")
    public ResponseEntity<Void> delete(@PathVariable String incidentId) {
        incidentService.delete(incidentId);
        return ResponseEntity.noContent().build();
    }
}
