package com.buildsmart.safety.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.safety.domain.model.InspectionStatus;
import com.buildsmart.safety.domain.model.SafetyInspection;
import com.buildsmart.safety.domain.repository.SafetyInspectionRepository;
import com.buildsmart.safety.service.SafetyInspectionService;
import com.buildsmart.safety.validator.SafetyInspectionValidator;
import com.buildsmart.safety.web.dto.InspectionDtos.CreateInspectionRequest;
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
public class SafetyInspectionServiceImpl implements SafetyInspectionService {

    private final SafetyInspectionRepository inspectionRepository;
    private final ProjectRepository projectRepository;
    private final SafetyInspectionValidator inspectionValidator;

    @Override
    public SafetyInspection create(CreateInspectionRequest request) {
        inspectionValidator.validate(request);
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        SafetyInspection last = inspectionRepository.findTopByOrderByInspectionIdDesc();
        SafetyInspection inspection = new SafetyInspection();
        inspection.setInspectionId(IdGeneratorUtil.nextInspectionId(last == null ? null : last.getInspectionId()));
        inspection.setProject(project);
        inspection.setOfficerId(request.officerId());
        inspection.setFindings(request.findings());
        inspection.setDate(LocalDate.now());
        inspection.setStatus(InspectionStatus.SCHEDULED);
        return inspectionRepository.save(inspection);
    }

    @Override
    @Transactional(readOnly = true)
    public SafetyInspection get(String id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SafetyInspection> search(Optional<String> projectId,
                                         Optional<InspectionStatus> status,
                                         Optional<LocalDate> dateFrom,
                                         Optional<LocalDate> dateTo,
                                         Pageable pageable) {

        Specification<SafetyInspection> spec = Specification.where((Specification<SafetyInspection>)null);

        if (projectId.isPresent())
            spec = spec.and((r, q, cb) -> cb.equal(r.get("project").get("projectId"), projectId.get()));
        if (status.isPresent())
            spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), status.get()));
        if (dateFrom.isPresent())
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("date"), dateFrom.get()));
        if (dateTo.isPresent())
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("date"), dateTo.get()));

        return inspectionRepository.findAll(spec, pageable);
    }

    @Override
    public SafetyInspection updateStatus(String id, InspectionStatus newStatus) {
        SafetyInspection inspection = get(id);
        inspection.setStatus(newStatus);
        return inspectionRepository.save(inspection);
    }

    @Override
    public void delete(String id) {
        if (!inspectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inspection not found: " + id);
        }
        inspectionRepository.deleteById(id);
    }
}
