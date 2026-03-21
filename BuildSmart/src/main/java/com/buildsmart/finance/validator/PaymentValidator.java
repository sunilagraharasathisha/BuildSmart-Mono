package com.buildsmart.finance.validator;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.finance.dto.PaymentRequestDto;
import com.buildsmart.finance.repository.InvoiceRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentValidator {

    private final InvoiceRepository invoiceRepository;

    public PaymentValidator(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public void validateCreate(PaymentRequestDto dto) {
        if (dto.getInvoiceId() == null || dto.getInvoiceId().isBlank()) {
            throw new IllegalArgumentException("Invoice ID must not be null");
        }
        if (!invoiceRepository.existsByInvoiceId(dto.getInvoiceId())) {
            throw new ResourceNotFoundException("Invoice", dto.getInvoiceId());
        }
        if (dto.getAmount() != null && dto.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
    }
}
