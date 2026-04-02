package com.buildsmart.projectmanager.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.dto.MilestoneRequest;
import com.buildsmart.projectmanager.dto.MilestoneResponse;
import com.buildsmart.projectmanager.entity.Milestone;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.MilestoneRepository;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.projectmanager.service.MilestoneService;
import com.buildsmart.projectmanager.validator.MilestoneValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneValidator milestoneValidator;

    @Override
    @Transactional
    public MilestoneResponse createMilestone(@Valid MilestoneRequest request) {
        milestoneValidator.validate(request);

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        if (milestoneRepository.existsByMilestoneNameIgnoreCaseAndProject_ProjectId(request.milestoneName(), request.projectId())) {
            throw new DuplicateResourceException("Milestone name already exists for this project: " + request.milestoneName());
        }

        String lastId = milestoneRepository.findTopByOrderByMilestoneIdDesc()
                .map(Milestone::getMilestoneId)
                .orElse(null);

        Milestone milestone = new Milestone();
        milestone.setMilestoneId(IdGeneratorUtil.nextMilestoneId(lastId));
        milestone.setProject(project);
        milestone.setMilestoneName(request.milestoneName());
        milestone.setDescription(request.description());
        milestone.setDueDate(request.dueDate());
        milestone.setAchievedDate(request.achievedDate());
        milestone.setStatus(request.status());

        return toResponse(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneResponse getMilestoneById(String milestoneId) {
        return toResponse(milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + milestoneId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestonesByProjectId(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return milestoneRepository.findByProject_ProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MilestoneResponse updateMilestone(String milestoneId, MilestoneRequest request) {
        milestoneValidator.validate(request);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + milestoneId));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        // Check if new milestone name is already in use by another milestone in the same project
        if (!milestone.getMilestoneName().equalsIgnoreCase(request.milestoneName()) &&
                milestoneRepository.existsByMilestoneNameIgnoreCaseAndProject_ProjectId(request.milestoneName(), request.projectId())) {
            throw new DuplicateResourceException("Milestone name already exists for this project: " + request.milestoneName());
        }

        milestone.setProject(project);
        milestone.setMilestoneName(request.milestoneName());
        milestone.setDescription(request.description());
        milestone.setDueDate(request.dueDate());
        milestone.setAchievedDate(request.achievedDate());
        milestone.setStatus(request.status());

        return toResponse(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    public void deleteMilestone(String milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + milestoneId));
        milestoneRepository.delete(milestone);
    }

    private MilestoneResponse toResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getMilestoneId(),
                milestone.getProject().getProjectId(),
                milestone.getMilestoneName(),
                milestone.getDescription(),
                milestone.getDueDate(),
                milestone.getAchievedDate(),
                milestone.getStatus()
        );
    }
}