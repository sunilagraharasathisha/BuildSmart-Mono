package com.buildsmart.finance.repository;

import com.buildsmart.finance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findTopByOrderByPaymentIdDesc();
}
