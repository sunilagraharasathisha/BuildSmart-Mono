package com.buildsmart.projectmanager.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.dto.ProjectRequest;
import com.buildsmart.projectmanager.dto.ProjectResponse;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.projectmanager.service.ProjectService;
import com.buildsmart.projectmanager.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        projectValidator.validate(request);
        if (projectRepository.existsByProjectNameIgnoreCase(request.projectName())) {
            throw new DuplicateResourceException("Project name already exists: " + request.projectName());
        }
        String lastId = projectRepository.findTopByOrderByProjectIdDesc()
                .map(Project::getProjectId)
                .orElse(null);
        Project project = new Project();
        project.setProjectId(IdGeneratorUtil.nextProjectId(lastId));
        project.setProjectName(request.projectName());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setBudget(request.budget());
        project.setStatus(request.status());
        return toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(String projectId) {
        return toResponse(projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getProjectId(),
                project.getProjectName(),
                project.getStartDate(),
                project.getEndDate(),
                project.getBudget(),
                project.getStatus()
        );
    }
}
