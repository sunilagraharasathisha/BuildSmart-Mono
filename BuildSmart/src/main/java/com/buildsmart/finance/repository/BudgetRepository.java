package com.buildsmart.finance.repository;

import com.buildsmart.common.enums.BudgetCategory;
import com.buildsmart.finance.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, String> {
    Budget findTopByOrderByBudgetIdDesc();
    boolean existsByProjectProjectIdAndCategory(String projectId, BudgetCategory category);
    List<Budget> findByProjectProjectId(String projectId);
}
