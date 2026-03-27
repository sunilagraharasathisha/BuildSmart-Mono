package com.buildsmart.siteops.repository;

import com.buildsmart.siteops.entity.ResourceRequestEntity;
import com.buildsmart.siteops.enums.ResourceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequestEntity, String> {

    /** Latest record — used for ID generation. */
    Optional<ResourceRequestEntity> findTopByOrderByRequestIdDesc();

    /** All requests for a project, newest first. */
    List<ResourceRequestEntity> findByProjectIdOrderByRequestedAtDesc(String projectId);

    /** Pending requests for a project — what PM needs to action. */
    List<ResourceRequestEntity> findByProjectIdAndStatusOrderByRequestedAtAsc(
            String projectId, ResourceRequestStatus status);

    /** All requests raised by a specific Site Engineer. */
    List<ResourceRequestEntity> findByRequestedByOrderByRequestedAtDesc(String requestedBy);

    /** All requests linked to a specific site log. */
    List<ResourceRequestEntity> findByLogIdOrderByRequestedAtDesc(String logId);
}
