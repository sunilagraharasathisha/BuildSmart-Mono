package com.buildsmart.finance.repository;

import com.buildsmart.common.enums.PaymentStatus;
import com.buildsmart.finance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findTopByOrderByPaymentIdDesc();
    boolean existsByInvoiceIdAndStatus(String invoiceId, PaymentStatus status);
}
