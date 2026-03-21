package com.buildsmart.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "task", uniqueConstraints = {
        @UniqueConstraint(name = "uq_task_id", columnNames = "task_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @Column(name = "task_id", length = 20, nullable = false, updatable = false)
    private String taskId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_project"))
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_department", nullable = false, length = 20)
    private AssignedDepartment assignedDepartment;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(length = 500)
    private String description;

    @Column(name = "planned_start", nullable = false)
    private LocalDate plannedStart;

    @Column(name = "planned_end", nullable = false)
    private LocalDate plannedEnd;

    @Column(name = "actual_start")
    private LocalDate actualStart;

    @Column(name = "actual_end")
    private LocalDate actualEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    public enum AssignedDepartment {
        FINANCE,   // FINBS001
        VENDOR,    // VENBS001
        SAFETY,    // SAFBS001
        SITE       // SITBS001
    }

    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
