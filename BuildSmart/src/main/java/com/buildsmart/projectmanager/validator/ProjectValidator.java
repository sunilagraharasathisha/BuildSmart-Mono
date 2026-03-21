package com.buildsmart.projectmanager.validator;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.projectmanager.dto.ProjectRequestDto;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public ProjectValidator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void validateCreate(ProjectRequestDto dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());
        validateBudget(dto.getBudget());
        if (projectRepository.existsByProjectName(dto.getProjectName())) {
            throw new DuplicateResourceException("Project", "name: " + dto.getProjectName());
        }
    }

    public void validateUpdate(String projectId, ProjectRequestDto dto) {
        if (!projectRepository.existsByProjectId(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
        validateDates(dto.getStartDate(), dto.getEndDate());
        validateBudget(dto.getBudget());
        projectRepository.findByProjectId(projectId).ifPresent(existing -> {
            if (!existing.getProjectName().equals(dto.getProjectName()) &&
                    projectRepository.existsByProjectName(dto.getProjectName())) {
                throw new DuplicateResourceException("Project", "name: " + dto.getProjectName());
            }
        });
    }

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (Objects.equals(startDate, endDate)) {
            throw new IllegalArgumentException("Start date and end date cannot be the same");
        }
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    private void validateBudget(java.math.BigDecimal budget) {
        if (budget == null || budget.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget must be greater than zero");
        }
    }
}
