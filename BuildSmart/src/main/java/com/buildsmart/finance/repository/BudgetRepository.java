package com.buildsmart.finance.repository;

import com.buildsmart.common.enums.BudgetCategory;
import com.buildsmart.finance.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, String> {

    Optional<Budget> findByBudgetId(String budgetId);

    boolean existsByBudgetId(String budgetId);

    Page<Budget> findByProject_ProjectId(String projectId, Pageable pageable);

    boolean existsByProject_ProjectIdAndCategory(String projectId, BudgetCategory category);

    Optional<Budget> findByProject_ProjectIdAndCategory(String projectId, BudgetCategory category);

    long countByBudgetIdStartingWith(String prefix);
}
