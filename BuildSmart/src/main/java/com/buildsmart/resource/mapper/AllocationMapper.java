package com.buildsmart.resource.mapper;

import com.buildsmart.resource.dto.AllocationResponse;
import com.buildsmart.resource.entity.AllocationEntity;
import com.buildsmart.resource.entity.ResourceEntity;

public final class AllocationMapper {

    private AllocationMapper() {}

    public static AllocationResponse toResponse(AllocationEntity a) {
        ResourceEntity r = a.getResource();
        return new AllocationResponse(
                a.getAllocationId(),
                a.getProjectId(),
                r != null ? r.getResourceId() : null,
                r != null ? r.getResourceName() : null,
                r != null ? r.getType() : null,
                r != null ? r.getAvailability() : null,
                r != null ? r.getCostRate() : null,
                a.getAssignedDate(),
                a.getReleasedDate(),
                a.getStatus(),
                a.getRequestNotes()
        );
    }
}
