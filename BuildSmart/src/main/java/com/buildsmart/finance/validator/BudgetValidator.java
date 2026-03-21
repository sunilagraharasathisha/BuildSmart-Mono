package com.buildsmart.finance.validator;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.finance.dto.BudgetRequestDto;
import com.buildsmart.finance.repository.BudgetRepository;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Component;

@Component
public class BudgetValidator {

    private final BudgetRepository budgetRepository;
    private final ProjectRepository projectRepository;

    public BudgetValidator(BudgetRepository budgetRepository, ProjectRepository projectRepository) {
        this.budgetRepository = budgetRepository;
        this.projectRepository = projectRepository;
    }

    public void validateCreate(BudgetRequestDto dto) {
        validateProjectExists(dto.getProjectId());
        validateAmounts(dto.getPlannedAmount(), dto.getActualAmount());
        if (budgetRepository.existsByProject_ProjectIdAndCategory(dto.getProjectId(), dto.getCategory())) {
            throw new DuplicateResourceException(
                    "Budget for category " + dto.getCategory() + " already exists for this project");
        }
    }

    public void validateUpdate(BudgetRequestDto dto, String budgetId) {
        validateProjectExists(dto.getProjectId());
        validateAmounts(dto.getPlannedAmount(), dto.getActualAmount());
    }

    private void validateProjectExists(String projectId) {
        if (!projectRepository.existsByProjectId(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
    }

    private void validateAmounts(java.math.BigDecimal plannedAmount, java.math.BigDecimal actualAmount) {
        if (plannedAmount != null && plannedAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Planned amount must not be negative");
        }
        if (actualAmount != null && actualAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Actual amount must not be negative");
        }
    }
}
