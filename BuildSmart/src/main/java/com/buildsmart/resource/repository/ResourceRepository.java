package com.buildsmart.resource.repository;

import com.buildsmart.resource.entity.ResourceEntity;
import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {

    Optional<ResourceEntity> findTopByOrderByResourceIdDesc();

    boolean existsByResourceNameIgnoreCase(String resourceName);

    List<ResourceEntity> findByType(ResourceType type);

    List<ResourceEntity> findByAvailability(ResourceAvailability availability);

    List<ResourceEntity> findByTypeAndAvailability(ResourceType type, ResourceAvailability availability);
}
