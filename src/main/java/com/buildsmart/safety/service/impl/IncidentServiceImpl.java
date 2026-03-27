package com.buildsmart.safety.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.safety.domain.model.Incident;
import com.buildsmart.safety.domain.model.IncidentSeverity;
import com.buildsmart.safety.domain.model.IncidentStatus;
import com.buildsmart.safety.domain.repository.IncidentRepository;
import com.buildsmart.safety.service.IncidentService;
import com.buildsmart.safety.validator.IncidentValidator;
import com.buildsmart.safety.web.dto.IncidentDtos.CreateIncidentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final ProjectRepository projectRepository;
    private final IncidentValidator incidentValidator;

    @Override
    public Incident create(CreateIncidentRequest request) {
        incidentValidator.validate(request);
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        Incident last = incidentRepository.findTopByOrderByIncidentIdDesc();
        Incident incident = new Incident();
        incident.setIncidentId(IdGeneratorUtil.nextIncidentId(last == null ? null : last.getIncidentId()));
        incident.setProject(project);
        incident.setDescription(request.description());
        incident.setSeverity(request.severity());
        incident.setReportedBy(request.reportedBy());
        incident.setDate(LocalDate.now());
        incident.setStatus(IncidentStatus.OPEN);
        return incidentRepository.save(incident);
    }

    @Override
    @Transactional(readOnly = true)
    public Incident get(String id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Incident> search(Optional<String> projectId,
                                 Optional<IncidentStatus> status,
                                 Optional<IncidentSeverity> severity,
                                 Optional<LocalDate> dateFrom,
                                 Optional<LocalDate> dateTo,
                                 Pageable pageable) {

        Specification<Incident> spec = Specification.where((Specification<Incident>)null);

        if (projectId.isPresent())
            spec = spec.and((r, q, cb) -> cb.equal(r.get("project").get("projectId"), projectId.get()));
        if (status.isPresent())
            spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), status.get()));
        if (severity.isPresent())
            spec = spec.and((r, q, cb) -> cb.equal(r.get("severity"), severity.get()));
        if (dateFrom.isPresent())
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("date"), dateFrom.get()));
        if (dateTo.isPresent())
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("date"), dateTo.get()));

        return incidentRepository.findAll(spec, pageable);
    }

    @Override
    public Incident updateStatus(String id, IncidentStatus newStatus) {
        Incident incident = get(id);
        incident.setStatus(newStatus);
        return incidentRepository.save(incident);
    }

    @Override
    public void delete(String id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incident not found: " + id);
        }
        incidentRepository.deleteById(id);
    }
}
