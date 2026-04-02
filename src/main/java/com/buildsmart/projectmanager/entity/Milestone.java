package com.buildsmart.projectmanager.entity;

import com.buildsmart.common.enums.MilestoneStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @Column(name = "milestone_id", nullable = false, updatable = false, length = 20)
    private String milestoneId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 120)
    private String milestoneName;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate achievedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MilestoneStatus status;
}