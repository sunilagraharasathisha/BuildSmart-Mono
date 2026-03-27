package com.buildsmart.resource.entity;

import com.buildsmart.resource.enums.ResourceAvailability;
import com.buildsmart.resource.enums.ResourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a deployable resource (Labor worker or Equipment unit) on a construction site.
 * Each resource tracks its type, current availability, and cost rate per day.
 */
@Getter
@Setter
@Entity
@Table(name = "resources")
public class ResourceEntity {

    @Id
    @Column(name = "resource_id", nullable = false, updatable = false, length = 20)
    private String resourceId;

    /**
     * Human-readable name, e.g. "Tower Crane #3" or "Mason - John Doe".
     */
    @Column(name = "resource_name", nullable = false, length = 120)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private ResourceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false, length = 15)
    private ResourceAvailability availability;

    /**
     * Daily cost rate in project currency.
     */
    @Column(name = "cost_rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal costRate;

    /**
     * Optional description / specs (e.g. "50-ton crane, max lift 40m").
     */
    @Column(name = "description", length = 300)
    private String description;
}
