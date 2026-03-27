package com.buildsmart.safety.service;

import com.buildsmart.safety.domain.model.Incident;
import com.buildsmart.safety.domain.model.IncidentSeverity;
import com.buildsmart.safety.domain.model.IncidentStatus;
import com.buildsmart.safety.web.dto.IncidentDtos.CreateIncidentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface IncidentService {

    Incident create(CreateIncidentRequest request);

    Incident get(String id);

    Page<Incident> search(Optional<String> projectId,
                          Optional<IncidentStatus> status,
                          Optional<IncidentSeverity> severity,
                          Optional<LocalDate> dateFrom,
                          Optional<LocalDate> dateTo,
                          Pageable pageable);

    Incident updateStatus(String id, IncidentStatus newStatus);

    void delete(String id);
}
