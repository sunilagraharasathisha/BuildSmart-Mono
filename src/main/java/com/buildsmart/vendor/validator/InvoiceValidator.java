package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.InvoiceRequest;
import org.springframework.stereotype.Component;

@Component
public class InvoiceValidator {

    public void validate(InvoiceRequest request) {
        if (request.amount().signum() <= 0) {
            throw new IllegalArgumentException("Invoice amount must be greater than zero");
        }
    }
}