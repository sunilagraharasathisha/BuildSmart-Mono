package com.buildsmart.resource.entity;

import com.buildsmart.resource.enums.AllocationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tracks the assignment of a ResourceEntity to a Project for a date range.
 * Business rules (overlap prevention, project cap, status transitions) are
 * enforced in AllocationServiceImpl.
 */
@Getter
@Setter
@Entity
@Table(name = "allocations")
public class AllocationEntity {

    @Id
    @Column(name = "allocation_id", nullable = false, updatable = false, length = 20)
    private String allocationId;

    /**
     * FK to Project (string ID like "CHEBS25001").
     * We store it as a plain String to avoid a circular dependency between modules.
     */
    @Column(name = "project_id", nullable = false, length = 20)
    private String projectId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_allocation_resource"))
    private ResourceEntity resource;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    /**
     * Nullable: open-ended allocation (resource stays until explicitly released).
     */
    @Column(name = "released_date")
    private LocalDate releasedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private AllocationStatus status;

    /**
     * Notes from the site engineer who requested this allocation.
     * Stored for traceability ("Need 3 masons for foundation pouring on Block B").
     */
    @Column(name = "request_notes", length = 500)
    private String requestNotes;
}
