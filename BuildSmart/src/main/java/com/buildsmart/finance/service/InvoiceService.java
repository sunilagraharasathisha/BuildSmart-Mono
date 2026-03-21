package com.buildsmart.finance.service;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.finance.dto.InvoiceRequestDto;
import com.buildsmart.finance.entity.Invoice;
import com.buildsmart.finance.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceIdGeneratorService invoiceIdGeneratorService;

    @Transactional
    public Invoice createInvoice(InvoiceRequestDto request) {
        if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
            throw new DuplicateResourceException("Invoice", "number: " + request.getInvoiceNumber());
        }
        String invoiceId = invoiceIdGeneratorService.generateNextInvoiceId();
        Invoice invoice = Invoice.builder()
                .invoiceId(invoiceId)
                .invoiceNumber(request.getInvoiceNumber())
                .build();
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
    }
}
