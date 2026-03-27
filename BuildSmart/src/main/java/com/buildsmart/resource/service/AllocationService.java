package com.buildsmart.resource.service;

import com.buildsmart.resource.dto.AllocationRequest;
import com.buildsmart.resource.dto.AllocationResponse;
import com.buildsmart.resource.dto.AllocationStatusChangeRequest;
import com.buildsmart.resource.dto.AllocationUpdateDatesRequest;

import java.util.List;

public interface AllocationService {

    AllocationResponse createAllocation(AllocationRequest request);

    AllocationResponse getAllocationById(String allocationId);

    List<AllocationResponse> getAllocationsByProject(String projectId);

    List<AllocationResponse> getAllocationsByResource(String resourceId);

    AllocationResponse updateDates(String allocationId, AllocationUpdateDatesRequest request);

    AllocationResponse changeStatus(String allocationId, AllocationStatusChangeRequest request);
}
