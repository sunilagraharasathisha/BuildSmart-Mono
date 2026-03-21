package com.buildsmart.finance.service;

import com.buildsmart.finance.dto.BudgetRequestDto;
import com.buildsmart.finance.dto.BudgetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BudgetService {

    BudgetResponseDto createBudget(BudgetRequestDto request);

    BudgetResponseDto getBudgetById(String budgetId);

    Page<BudgetResponseDto> getBudgetsByProject(String projectId, Pageable pageable);

    BudgetResponseDto updateBudget(String budgetId, BudgetRequestDto request);

    void deleteBudget(String budgetId);
}
