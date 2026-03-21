package com.buildsmart.finance.service;

import com.buildsmart.finance.dto.ExpenseRequest;
import com.buildsmart.finance.dto.ExpenseResponse;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request);
    List<ExpenseResponse> getExpensesByProjectId(String projectId);
}
