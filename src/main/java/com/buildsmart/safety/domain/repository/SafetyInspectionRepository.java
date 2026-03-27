package com.buildsmart.safety.domain.repository;

import com.buildsmart.safety.domain.model.InspectionStatus;
import com.buildsmart.safety.domain.model.SafetyInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SafetyInspectionRepository
        extends JpaRepository<SafetyInspection, String>, JpaSpecificationExecutor<SafetyInspection> {

    List<SafetyInspection> findByProjectProjectId(String projectId);

    List<SafetyInspection> findByStatus(InspectionStatus status);

    SafetyInspection findTopByOrderByInspectionIdDesc();
}
