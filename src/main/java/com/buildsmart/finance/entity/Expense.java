package com.buildsmart.finance.entity;

import com.buildsmart.common.enums.ExpenseStatus;
import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @Column(name = "expense_id", nullable = false, updatable = false, length = 20)
    private String expenseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 120)
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseStatus status;
}
