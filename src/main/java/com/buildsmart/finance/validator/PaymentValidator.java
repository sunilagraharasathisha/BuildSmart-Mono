package com.buildsmart.finance.validator;

import com.buildsmart.finance.dto.PaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentValidator {
    public void validate(PaymentRequest request) {
        if (request.amount().signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
    }
}
