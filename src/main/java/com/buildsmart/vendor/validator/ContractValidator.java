package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.ContractRequest;
import org.springframework.stereotype.Component;

@Component
public class ContractValidator {

    public void validate(ContractRequest request) {
        if (request.value().signum() <= 0) {
            throw new IllegalArgumentException("Contract value must be greater than zero");
        }

        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Contract end date cannot be before start date");
        }
    }
}