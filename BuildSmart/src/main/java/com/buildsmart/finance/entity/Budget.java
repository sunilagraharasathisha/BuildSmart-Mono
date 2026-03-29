package com.buildsmart.finance.entity;

import com.buildsmart.common.enums.BudgetCategory;
import com.buildsmart.common.enums.BudgetStatus;
import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "budgets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_budget_project_category", columnNames = { "project_id", "category" })
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
    private BigDecimal actualAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_status", nullable = false, length = 30)
    private BudgetStatus status;

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount != null ? plannedAmount : BigDecimal.ZERO;
        recalculateStatus();
        recalculateVariance();
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount != null ? actualAmount : BigDecimal.ZERO;
        recalculateStatus();
        recalculateVariance();
    }

    private void recalculateVariance() {
        if (this.plannedAmount != null && this.actualAmount != null) {
            this.variance = this.actualAmount.subtract(this.plannedAmount);
        }
    }

    private void recalculateStatus() {
        if (this.plannedAmount == null || this.plannedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = BudgetStatus.UNDER_BUDGET;
            return;
        }

        if (this.actualAmount == null) {
            this.actualAmount = BigDecimal.ZERO;
        }

        BigDecimal lowerBound = this.plannedAmount.multiply(new BigDecimal("0.8"));

        if (this.actualAmount.compareTo(this.plannedAmount) > 0) {
            this.status = BudgetStatus.OVER_BUDGET;
        } else if (this.actualAmount.compareTo(lowerBound) >= 0) {
            this.status = BudgetStatus.NEAR_BUDGET;
        } else {
            this.status = BudgetStatus.UNDER_BUDGET;
        }
    }

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal variance;
}
