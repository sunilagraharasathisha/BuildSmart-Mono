package com.buildsmart.finance.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
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

import java.math.BigDecimal;
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
        ApplicationLogger.log.info("Creating the budget");
        budgetValidator.validate(request);
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));
        if (budgetRepository.existsByProjectProjectIdAndCategory(request.projectId(), request.category())) {
            throw new DuplicateResourceException(
                    "Budget category already exists for this project: " + request.category());
        }

        if (request.plannedAmount().compareTo(project.getBudget()) > 0) {
            throw new IllegalArgumentException("Planned budget exceeds project budget.");
        }

        BigDecimal actualAmount = request.actualAmount() != null
                ? request.actualAmount()
                : BigDecimal.ZERO;

        Budget last = budgetRepository.findTopByOrderByBudgetIdDesc();
        Budget budget = new Budget();
        budget.setBudgetId(IdGeneratorUtil.nextBudgetId(last == null ? null : last.getBudgetId()));
        budget.setProject(project);
        budget.setCategory(request.category());
        budget.setPlannedAmount(request.plannedAmount());
        budget.setActualAmount(actualAmount);
        budget.setVariance(actualAmount.subtract(request.plannedAmount()));
        // status computed via entity setter
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
                budget.getVariance(),
                budget.getStatus());
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(String budgetId, BudgetRequest request) {
        ApplicationLogger.log.info("Updating the budget");

        budgetValidator.validate(request);

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found: " + budgetId));

        BigDecimal actualAmount = request.actualAmount() != null
                ? request.actualAmount()
                : BigDecimal.ZERO;

        if (request.plannedAmount().compareTo(budget.getProject().getBudget()) > 0) {
            throw new IllegalArgumentException("Planned budget exceeds project budget.");
        }

        budget.setCategory(request.category());
        budget.setPlannedAmount(request.plannedAmount());
        budget.setActualAmount(actualAmount);
        budget.setVariance(actualAmount.subtract(request.plannedAmount()));

        return toResponse(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    public void deleteBudget(String budgetId) {
        ApplicationLogger.log.info("Deleting the budget");

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found: " + budgetId));

        budgetRepository.delete(budget);
    }

    @Override
    public void validatePlannedBudget(String projectId, java.math.BigDecimal plannedAmount) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        if (plannedAmount.compareTo(project.getBudget()) > 0) {
            throw new IllegalArgumentException("Planned budget exceeds project budget.");
        }
    }
}
