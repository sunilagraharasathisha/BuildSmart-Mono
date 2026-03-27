package com.buildsmart.safety.web.dto;

import com.buildsmart.safety.domain.model.IncidentSeverity;
import com.buildsmart.safety.domain.model.IncidentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class IncidentDtos {

    public record CreateIncidentRequest(
            @NotBlank String projectId,
            @NotBlank @Size(max = 5000) String description,
            @NotNull IncidentSeverity severity,
            @NotBlank String reportedBy
    ) {}

    public record UpdateIncidentStatusRequest(
            @NotNull IncidentStatus status
    ) {}

    public record IncidentResponse(
            String incidentId,
            String projectId,
            LocalDate date,
            String description,
            IncidentSeverity severity,
            String reportedBy,
            IncidentStatus status
    ) {}
}
