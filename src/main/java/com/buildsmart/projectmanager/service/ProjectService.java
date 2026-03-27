package com.buildsmart.projectmanager.service;

import com.buildsmart.projectmanager.dto.ProjectRequest;
import com.buildsmart.projectmanager.dto.ProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(ProjectRequest request);
    ProjectResponse getProjectById(String projectId);
    List<ProjectResponse> getAllProjects();
    ProjectResponse updateProject(String projectId, ProjectRequest request);
    void deleteProject(String projectId);
}
