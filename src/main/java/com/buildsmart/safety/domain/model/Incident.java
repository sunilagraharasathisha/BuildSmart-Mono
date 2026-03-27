package com.buildsmart.safety.domain.model;

import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @Column(name = "incident_id", nullable = false, updatable = false, length = 20)
    private String incidentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 16, nullable = false)
    private IncidentSeverity severity;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Stores the userId (e.g. "USRBS001") of the person who reported this incident.
     */
    @Column(name = "reported_by", nullable = false, length = 20)
    private String reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private IncidentStatus status = IncidentStatus.OPEN;
}
