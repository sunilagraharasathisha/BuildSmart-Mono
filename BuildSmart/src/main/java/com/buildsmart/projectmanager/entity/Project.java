package com.buildsmart.projectmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project", uniqueConstraints = {
        @UniqueConstraint(name = "uq_project_id", columnNames = "project_id"),
        @UniqueConstraint(name = "uq_project_name", columnNames = "project_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @Column(name = "project_id", length = 20, nullable = false, updatable = false)
    private String projectId;

    @Column(name = "project_name", nullable = false, unique = true, length = 150)
    private String projectName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    public enum ProjectStatus {
        PLANNED, IN_PROGRESS, COMPLETED, ON_HOLD, CANCELLED
    }
}
