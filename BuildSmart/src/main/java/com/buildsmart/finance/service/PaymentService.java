package com.buildsmart.finance.service;

import com.buildsmart.finance.dto.PaymentRequestDto;
import com.buildsmart.finance.dto.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto request);

    PaymentResponseDto getPaymentById(String paymentId);

    List<PaymentResponseDto> getPaymentsByInvoice(String invoiceId);

    Page<PaymentResponseDto> getAllPayments(Pageable pageable);

    PaymentResponseDto updatePayment(String paymentId, PaymentRequestDto request);

    void deletePayment(String paymentId);
}
