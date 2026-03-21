package com.buildsmart.finance.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
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
        Expense last = expenseRepository.findTopByOrderByExpenseIdDesc();
        Expense expense = new Expense();
        expense.setExpenseId(IdGeneratorUtil.nextExpenseId(last == null ? null : last.getExpenseId()));
        expense.setProject(project);
        expense.setDescription(request.description());
        expense.setDate(request.date());
        expense.setApprovedBy(request.approvedBy());
        expense.setStatus(request.status());
        return toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByProjectId(String projectId) {
        return expenseRepository.findByProjectProjectId(projectId).stream().map(this::toResponse).toList();
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getExpenseId(),
                expense.getProject().getProjectId(),
                expense.getDescription(),
                expense.getDate(),
                expense.getApprovedBy(),
                expense.getStatus()
        );
    }
}
