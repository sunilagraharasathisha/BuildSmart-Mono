package com.buildsmart.projectmanager.service;

import com.buildsmart.projectmanager.dto.ProjectRequestDto;
import com.buildsmart.projectmanager.dto.ProjectResponseDto;
import com.buildsmart.projectmanager.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectResponseDto createProject(ProjectRequestDto request);

    ProjectResponseDto getProjectById(String projectId);

    Page<ProjectResponseDto> getAllProjects(Pageable pageable);

    ProjectResponseDto updateProject(String projectId, ProjectRequestDto request);

    void deleteProject(String projectId);

    Project getProjectEntity(String projectId);
}
