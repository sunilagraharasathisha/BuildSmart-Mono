package com.buildsmart.projectmanager.validator;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.projectmanager.dto.TaskRequestDto;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TaskValidator {

    private final ProjectRepository projectRepository;

    public TaskValidator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void validateCreate(TaskRequestDto dto) {
        validateProjectExists(dto.getProjectId());
        validatePlannedDates(dto.getPlannedStart(), dto.getPlannedEnd());
    }

    public void validateUpdate(TaskRequestDto dto) {
        validateProjectExists(dto.getProjectId());
        validatePlannedDates(dto.getPlannedStart(), dto.getPlannedEnd());
    }

    private void validateProjectExists(String projectId) {
        if (!projectRepository.existsByProjectId(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
    }

    private void validatePlannedDates(java.time.LocalDate plannedStart, java.time.LocalDate plannedEnd) {
        if (plannedStart == null || plannedEnd == null) return;
        if (plannedEnd.isBefore(plannedStart) || plannedEnd.isEqual(plannedStart)) {
            throw new IllegalArgumentException("Planned end date must be after planned start date");
        }
    }
}
