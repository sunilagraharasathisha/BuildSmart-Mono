package com.buildsmart.finance.service;

import com.buildsmart.finance.dto.PaymentRequest;
import com.buildsmart.finance.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
}
