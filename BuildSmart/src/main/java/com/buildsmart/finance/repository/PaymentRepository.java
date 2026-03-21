package com.buildsmart.finance.repository;

import com.buildsmart.finance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByPaymentId(String paymentId);

    boolean existsByPaymentId(String paymentId);

    List<Payment> findByInvoice_InvoiceId(String invoiceId);

    long countByPaymentIdStartingWith(String prefix);
}
