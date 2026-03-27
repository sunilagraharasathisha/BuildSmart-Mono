package com.buildsmart.safety.web.controller;

import com.buildsmart.safety.domain.model.InspectionStatus;
import com.buildsmart.safety.service.SafetyInspectionService;
import com.buildsmart.safety.web.dto.InspectionDtos.CreateInspectionRequest;
import com.buildsmart.safety.web.dto.InspectionDtos.InspectionResponse;
import com.buildsmart.safety.web.dto.InspectionDtos.UpdateInspectionStatusRequest;
import com.buildsmart.safety.web.dto.SafetyPageResponse;
import com.buildsmart.safety.web.mapper.InspectionMapper;
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
@RequestMapping("/api/v1/safety/inspections")
@RequiredArgsConstructor
@Tag(name = "Safety - Inspections", description = "Safety inspection scheduling and management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'SAFETY_OFFICER', 'SITE_ENGINEER')")
public class SafetyInspectionController {

    private final SafetyInspectionService inspectionService;

    @PostMapping
    @Operation(summary = "Schedule a new safety inspection")
    @ApiResponse(responseCode = "201", description = "Inspection scheduled successfully",
            content = @Content(schema = @Schema(implementation = InspectionResponse.class)))
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<InspectionResponse> create(@Valid @RequestBody CreateInspectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InspectionMapper.toResponse(inspectionService.create(request)));
    }

    @GetMapping("/{inspectionId}")
    @Operation(summary = "Get inspection by ID")
    @ApiResponse(responseCode = "200", description = "Inspection fetched")
    @ApiResponse(responseCode = "404", description = "Inspection not found")
    public ResponseEntity<InspectionResponse> get(@PathVariable String inspectionId) {
        return ResponseEntity.ok(InspectionMapper.toResponse(inspectionService.get(inspectionId)));
    }

    @GetMapping
    @Operation(summary = "Search inspections with optional filters and pagination")
    @ApiResponse(responseCode = "200", description = "Inspections fetched")
    public ResponseEntity<SafetyPageResponse<InspectionResponse>> search(
            @RequestParam Optional<String> projectId,
            @RequestParam Optional<InspectionStatus> status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date,desc") String sort
    ) {
        Sort sortObj = Sort.by(sort.split(",")[0]);
        if (sort.endsWith(",desc")) sortObj = sortObj.descending();

        Page<InspectionResponse> result = inspectionService
                .search(projectId, status, dateFrom, dateTo, PageRequest.of(page, size, sortObj))
                .map(InspectionMapper::toResponse);

        return ResponseEntity.ok(new SafetyPageResponse<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        ));
    }

    @PatchMapping("/{inspectionId}/status")
    @Operation(summary = "Update inspection status")
    @ApiResponse(responseCode = "200", description = "Status updated")
    @ApiResponse(responseCode = "404", description = "Inspection not found")
    public ResponseEntity<InspectionResponse> updateStatus(
            @PathVariable String inspectionId,
            @Valid @RequestBody UpdateInspectionStatusRequest request) {
        return ResponseEntity.ok(
                InspectionMapper.toResponse(inspectionService.updateStatus(inspectionId, request.status()))
        );
    }

    @DeleteMapping("/{inspectionId}")
    @Operation(summary = "Delete a safety inspection")
    @ApiResponse(responseCode = "204", description = "Inspection deleted")
    @ApiResponse(responseCode = "404", description = "Inspection not found")
    @PreAuthorize("hasAnyRole('ADMIN', 'SAFETY_OFFICER')")
    public ResponseEntity<Void> delete(@PathVariable String inspectionId) {
        inspectionService.delete(inspectionId);
        return ResponseEntity.noContent().build();
    }
}
