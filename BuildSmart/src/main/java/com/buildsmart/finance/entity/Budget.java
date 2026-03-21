package com.buildsmart.finance.entity;

import com.buildsmart.common.enums.BudgetCategory;
import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "budget", uniqueConstraints = {
        @UniqueConstraint(name = "uq_budget_id", columnNames = "budget_id"),
        @UniqueConstraint(name = "uq_budget_project_category", columnNames = {"project_id", "category"})
}, indexes = {
        @Index(name = "ix_budget_project", columnList = "project_id"),
        @Index(name = "ix_budget_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @Column(name = "budget_id", length = 20, nullable = false, updatable = false)
    private String budgetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_budget_project"))
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BudgetCategory category;

    @Column(name = "planned_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal plannedAmount;

    @Column(name = "actual_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal actualAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal variance;

    @PrePersist
    @PreUpdate
    public void calculateVariance() {
        if (plannedAmount == null) plannedAmount = BigDecimal.ZERO;
        if (actualAmount == null) actualAmount = BigDecimal.ZERO;
        this.variance = plannedAmount.subtract(actualAmount);
    }
}
