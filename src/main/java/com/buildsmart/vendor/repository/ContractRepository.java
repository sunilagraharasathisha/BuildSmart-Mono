package com.buildsmart.vendor.repository;

import com.buildsmart.vendor.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, String> {

    Contract findTopByOrderByContractIdDesc();

    List<Contract> findByVendorVendorId(String vendorId);

    List<Contract> findByProjectProjectId(String projectId);

    List<Contract> findByStatus(String status);
}