package com.buildsmart.vendor.entity;

import com.buildsmart.projectmanager.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @Column(name = "contract_id", nullable = false, updatable = false, length = 20)
    private String contractId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal value;

    @Column(nullable = false, length = 30)
    private String status;
}
