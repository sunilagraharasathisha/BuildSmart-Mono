package com.buildsmart.vendor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @Column(name = "vendor_id", nullable = false, updatable = false, length = 20)
    private String vendorId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @Column(nullable = false, length = 30)
    private String status;
}