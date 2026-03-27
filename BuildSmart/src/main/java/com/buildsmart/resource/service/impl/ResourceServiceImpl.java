package com.buildsmart.resource.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.resource.dto.ResourceRequest;
import com.buildsmart.resource.dto.ResourceResponse;
import com.buildsmart.resource.entity.ResourceEntity;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;
import com.buildsmart.resource.repository.ResourceRepository;
import com.buildsmart.resource.service.ResourceService;
import com.buildsmart.resource.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceValidator resourceValidator;

    @Override
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        resourceValidator.validate(request);

        if (resourceRepository.existsByResourceNameIgnoreCase(request.resourceName())) {
            throw new DuplicateResourceException(
                    "A resource with name '" + request.resourceName() + "' already exists");
        }

        String lastId = resourceRepository.findTopByOrderByResourceIdDesc()
                .map(ResourceEntity::getResourceId)
                .orElse(null);

        ResourceEntity entity = new ResourceEntity();
        entity.setResourceId(IdGeneratorUtil.nextResourceId(lastId));
        entity.setResourceName(request.resourceName());
        entity.setType(request.type());
        entity.setAvailability(request.availability());
        entity.setCostRate(request.costRate());
        entity.setDescription(request.description());

        return toResponse(resourceRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(String resourceId) {
        return toResponse(find(resourceId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByType(ResourceType type) {
        return resourceRepository.findByType(type).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getAvailableResources(ResourceType type) {
        List<ResourceEntity> result = (type != null)
                ? resourceRepository.findByTypeAndAvailability(type, ResourceAvailability.Available)
                : resourceRepository.findByAvailability(ResourceAvailability.Available);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ResourceResponse updateAvailability(String resourceId, ResourceAvailability availability) {
        ResourceEntity entity = find(resourceId);
        entity.setAvailability(availability);
        return toResponse(resourceRepository.save(entity));
    }

    /* ── helpers ── */

    private ResourceEntity find(String resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceId));
    }

    private ResourceResponse toResponse(ResourceEntity e) {
        return new ResourceResponse(
                e.getResourceId(),
                e.getResourceName(),
                e.getType(),
                e.getAvailability(),
                e.getCostRate(),
                e.getDescription()
        );
    }
}
