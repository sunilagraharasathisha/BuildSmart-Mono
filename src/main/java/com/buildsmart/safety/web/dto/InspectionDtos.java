package com.buildsmart.safety.web.dto;

import com.buildsmart.safety.domain.model.InspectionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class InspectionDtos {

    public record CreateInspectionRequest(
            @NotBlank String projectId,
            @NotBlank String officerId,
            @Size(max = 5000) String findings
    ) {}

    public record UpdateInspectionStatusRequest(
            @NotNull InspectionStatus status
    ) {}

    public record InspectionResponse(
            String inspectionId,
            String projectId,
            LocalDate date,
            String officerId,
            String findings,
            InspectionStatus status
    ) {}
}
