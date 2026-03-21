package com.buildsmart.finance.repository;

import com.buildsmart.finance.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
    Expense findTopByOrderByExpenseIdDesc();
    List<Expense> findByProjectProjectId(String projectId);
}
