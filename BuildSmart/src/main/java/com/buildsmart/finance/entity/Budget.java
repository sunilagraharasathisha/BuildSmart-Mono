package com.buildsmart.finance.entity;

import com.buildsmart.common.enums.BudgetCategory;
import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "budgets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_budget_project_category", columnNames = {"project_id", "category"})
})
public class Budget {
    @Id
    @Column(name = "budget_id", nullable = false, updatable = false, length = 20)
    private String budgetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BudgetCategory category;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal plannedAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal actualAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal variance;
}
