package com.buildsmart.finance.repository;

import com.buildsmart.finance.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceId(String invoiceId);

    boolean existsByInvoiceId(String invoiceId);

    boolean existsByInvoiceNumber(String invoiceNumber);

    long countByInvoiceIdStartingWith(String prefix);
}
