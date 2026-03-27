package com.buildsmart.resource.service;

import com.buildsmart.resource.dto.ResourceRequest;
import com.buildsmart.resource.dto.ResourceResponse;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse getResourceById(String resourceId);

    List<ResourceResponse> getAllResources();

    List<ResourceResponse> getResourcesByType(ResourceType type);

    List<ResourceResponse> getAvailableResources(ResourceType type);

    ResourceResponse updateAvailability(String resourceId, ResourceAvailability availability);
}
