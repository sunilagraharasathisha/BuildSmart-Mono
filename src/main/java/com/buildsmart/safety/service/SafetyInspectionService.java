package com.buildsmart.safety.service;

import com.buildsmart.safety.domain.model.InspectionStatus;
import com.buildsmart.safety.domain.model.SafetyInspection;
import com.buildsmart.safety.web.dto.InspectionDtos.CreateInspectionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface SafetyInspectionService {

    SafetyInspection create(CreateInspectionRequest request);

    SafetyInspection get(String id);

    Page<SafetyInspection> search(Optional<String> projectId,
                                  Optional<InspectionStatus> status,
                                  Optional<LocalDate> dateFrom,
                                  Optional<LocalDate> dateTo,
                                  Pageable pageable);

    SafetyInspection updateStatus(String id, InspectionStatus newStatus);

    void delete(String id);
}
