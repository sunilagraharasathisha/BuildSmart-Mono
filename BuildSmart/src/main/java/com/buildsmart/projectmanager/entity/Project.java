package com.buildsmart.projectmanager.entity;

import com.buildsmart.common.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "projects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_project_name", columnNames = "project_name")
})
public class Project {

    @Id
    @Column(name = "project_id", nullable = false, updatable = false, length = 20)
    private String projectId;

    @Column(name = "project_name", nullable = false, unique = true, length = 120)
    private String projectName;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal budget;

    @Column(name = "project_manager_id", length = 50)
    private String projectManagerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}
