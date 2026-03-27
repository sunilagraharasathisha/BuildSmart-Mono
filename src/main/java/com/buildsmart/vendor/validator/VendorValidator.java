package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.VendorRequest;
import org.springframework.stereotype.Component;

@Component
public class VendorValidator {

    public void validate(VendorRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor name cannot be empty");
        }

        if (request.status() == null || request.status().trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor status cannot be empty");
        }
    }
}