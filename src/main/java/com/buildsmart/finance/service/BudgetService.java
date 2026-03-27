package com.buildsmart.finance.service;

import com.buildsmart.finance.dto.BudgetRequest;
import com.buildsmart.finance.dto.BudgetResponse;

import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(BudgetRequest request);

    List<BudgetResponse> getBudgetsByProjectId(String projectId);

    BudgetResponse updateBudget(String budgetId, BudgetRequest request);

    void deleteBudget(String budgetId);
}
