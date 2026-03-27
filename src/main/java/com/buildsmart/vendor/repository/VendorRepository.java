package com.buildsmart.vendor.repository;

import com.buildsmart.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, String> {

    Vendor findTopByOrderByVendorIdDesc();

    boolean existsByName(String name);

    List<Vendor> findByStatus(String status);
}