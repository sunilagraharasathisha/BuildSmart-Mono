package com.buildsmart.finance.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.dto.BudgetRequest;
import com.buildsmart.finance.dto.BudgetResponse;
import com.buildsmart.finance.entity.Budget;
import com.buildsmart.finance.repository.BudgetRepository;
import com.buildsmart.finance.service.BudgetService;
import com.buildsmart.finance.validator.BudgetValidator;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ProjectRepository projectRepository;
    private final BudgetValidator budgetValidator;

    @Override
    @Transactional
    public BudgetResponse createBudget(BudgetRequest request) {
        budgetValidator.validate(request);
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));
        if (budgetRepository.existsByProjectProjectIdAndCategory(request.projectId(), request.category())) {
            throw new DuplicateResourceException("Budget category already exists for this project: " + request.category());
        }
        Budget last = budgetRepository.findTopByOrderByBudgetIdDesc();
        Budget budget = new Budget();
        budget.setBudgetId(IdGeneratorUtil.nextBudgetId(last == null ? null : last.getBudgetId()));
        budget.setProject(project);
        budget.setCategory(request.category());
        budget.setPlannedAmount(request.plannedAmount());
        budget.setActualAmount(request.actualAmount());
        budget.setVariance(request.actualAmount().subtract(request.plannedAmount()));
        return toResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByProjectId(String projectId) {
        return budgetRepository.findByProjectProjectId(projectId).stream().map(this::toResponse).toList();
    }

    private BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getBudgetId(),
                budget.getProject().getProjectId(),
                budget.getCategory(),
                budget.getPlannedAmount(),
                budget.getActualAmount(),
                budget.getVariance()
        );
    }
}
