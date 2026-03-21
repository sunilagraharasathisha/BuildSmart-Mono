package com.buildsmart.finance.repository;

import com.buildsmart.common.enums.ExpenseStatus;
import com.buildsmart.finance.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, String> {

    Optional<Expense> findByExpenseId(String expenseId);

    boolean existsByExpenseId(String expenseId);

    Page<Expense> findByProject_ProjectId(String projectId, Pageable pageable);

    Page<Expense> findByStatus(ExpenseStatus status, Pageable pageable);

    long countByExpenseIdStartingWith(String prefix);
}
