package com.buildsmart.finance.validator;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.finance.dto.ExpenseRequestDto;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Component;

@Component
public class ExpenseValidator {

    private final ProjectRepository projectRepository;

    public ExpenseValidator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void validateCreate(ExpenseRequestDto dto) {
        validateProjectExists(dto.getProjectId());
    }

    private void validateProjectExists(String projectId) {
        if (!projectRepository.existsByProjectId(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
    }
}
