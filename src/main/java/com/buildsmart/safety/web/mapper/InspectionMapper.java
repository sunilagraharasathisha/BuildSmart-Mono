package com.buildsmart.safety.web.mapper;

import com.buildsmart.safety.domain.model.SafetyInspection;
import com.buildsmart.safety.web.dto.InspectionDtos.InspectionResponse;

public class InspectionMapper {

    public static InspectionResponse toResponse(SafetyInspection inspection) {
        return new InspectionResponse(
                inspection.getInspectionId(),
                inspection.getProject().getProjectId(),
                inspection.getDate(),
                inspection.getOfficerId(),
                inspection.getFindings(),
                inspection.getStatus()
        );
    }
}
