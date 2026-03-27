package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.DeliveryRequest;
import org.springframework.stereotype.Component;

@Component
public class DeliveryValidator {

    public void validate(DeliveryRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Delivery quantity must be greater than zero");
        }

        if (request.item() == null || request.item().trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery item cannot be empty");
        }
    }
}