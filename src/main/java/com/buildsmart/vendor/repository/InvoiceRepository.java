package com.buildsmart.vendor.repository;

import com.buildsmart.vendor.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    Invoice findTopByOrderByInvoiceIdDesc();

    List<Invoice> findByContractContractId(String contractId);

    List<Invoice> findByStatus(String status);
}
