package com.buildsmart.safety.domain.model;

import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "safety_inspections")
public class SafetyInspection {

    @Id
    @Column(name = "inspection_id", nullable = false, updatable = false, length = 20)
    private String inspectionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Stores the userId (e.g. "USRBS001") of the safety officer conducting this inspection.
     */
    @Column(name = "officer_id", nullable = false, length = 20)
    private String officerId;

    @Column(name = "findings", columnDefinition = "TEXT")
    private String findings;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private InspectionStatus status = InspectionStatus.SCHEDULED;
}
