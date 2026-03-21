package com.buildsmart.finance.service;

import com.buildsmart.finance.dto.ExpenseRequestDto;
import com.buildsmart.finance.dto.ExpenseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseService {

    ExpenseResponseDto createExpense(ExpenseRequestDto request);

    ExpenseResponseDto getExpenseById(String expenseId);

    Page<ExpenseResponseDto> getExpensesByProject(String projectId, Pageable pageable);

    Page<ExpenseResponseDto> getPendingExpenses(Pageable pageable);

    ExpenseResponseDto updateExpense(String expenseId, ExpenseRequestDto request);

    void deleteExpense(String expenseId);
}
