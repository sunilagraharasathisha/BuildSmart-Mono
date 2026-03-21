package com.buildsmart.finance.entity;

import com.buildsmart.common.enums.ExpenseStatus;
import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense", uniqueConstraints = {
        @UniqueConstraint(name = "uq_expense_id", columnNames = "expense_id")
}, indexes = {
        @Index(name = "ix_expense_project", columnList = "project_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @Column(name = "expense_id", length = 20, nullable = false, updatable = false)
    private String expenseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_expense_project"))
    private Project project;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseStatus status;
}
