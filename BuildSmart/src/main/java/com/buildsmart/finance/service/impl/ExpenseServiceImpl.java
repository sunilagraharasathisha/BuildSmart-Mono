package com.buildsmart.finance.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.dto.ExpenseRequest;
import com.buildsmart.finance.dto.ExpenseResponse;
import com.buildsmart.finance.entity.Expense;
import com.buildsmart.finance.repository.ExpenseRepository;
import com.buildsmart.finance.service.ExpenseService;
import com.buildsmart.finance.validator.ExpenseValidator;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProjectRepository projectRepository;
    private final ExpenseValidator expenseValidator;

    @Override
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        expenseValidator.validate(request);
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        if (project.getStartDate() != null && request.date().isBefore(project.getStartDate()) ||
                project.getEndDate() != null && request.date().isAfter(project.getEndDate())) {
            throw new IllegalArgumentException("Expense date must be within project duration.");
        }

        Expense last = expenseRepository.findTopByOrderByExpenseIdDesc();
        Expense expense = new Expense();
        expense.setExpenseId(IdGeneratorUtil.nextExpenseId(last == null ? null : last.getExpenseId()));
        expense.setProject(project);
        expense.setDescription(request.description());
        expense.setDate(request.date());
        expense.setApprovedBy(request.approvedBy());
        expense.setStatus(com.buildsmart.common.enums.ExpenseStatus.PENDING);

        String pmId = project.getProjectManagerId();
        ApplicationLogger.log.info("Notifying project manager {} for expense {}", pmId, expense.getExpenseId());

        return toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByProjectId(String projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found: " + projectId);
        }

        return expenseRepository.findByProjectProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getExpenseId(),
                expense.getProject().getProjectId(),
                expense.getDescription(),
                expense.getDate(),
                expense.getApprovedBy(),
                expense.getStatus());
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(String expenseId, ExpenseRequest request) {
        expenseValidator.validate(request);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found: " + expenseId));

        expense.setDescription(request.description());
        expense.setDate(request.date());
        expense.setApprovedBy(request.approvedBy());
        expense.setStatus(request.status());

        return toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public void deleteExpense(String expenseId) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found: " + expenseId));

        expenseRepository.delete(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse approveExpense(String expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + expenseId));
        expense.setStatus(com.buildsmart.common.enums.ExpenseStatus.APPROVED);
        return toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public ExpenseResponse rejectExpense(String expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + expenseId));
        expense.setStatus(com.buildsmart.common.enums.ExpenseStatus.REJECTED);
        return toResponse(expenseRepository.save(expense));
    }
}
