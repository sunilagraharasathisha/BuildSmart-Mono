package com.buildsmart.vendor.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.vendor.entity.Invoice;
import com.buildsmart.vendor.entity.Contract;
import com.buildsmart.vendor.dto.InvoiceRequest;
import com.buildsmart.vendor.dto.InvoiceResponse;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.InvoiceRepository;
import com.buildsmart.vendor.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        ApplicationLogger.log.info("Creating invoice");

        Contract contract = contractRepository.findById(request.contractId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found: " + request.contractId()));

        Invoice last = invoiceRepository.findTopByOrderByInvoiceIdDesc();

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(
                IdGeneratorUtil.nextInvoiceId(last == null ? null : last.getInvoiceId()));
        invoice.setContract(contract);
        invoice.setAmount(request.amount());
        invoice.setDate(request.date());
        invoice.setStatus("UNPAID");

        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByContractId(String contractId) {
        return invoiceRepository
                .findByContractContractId(contractId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.of(
                invoice.getInvoiceId(),
                invoice.getContract().getContractId(),
                invoice.getAmount(),
                invoice.getDate(),
                invoice.getStatus()
        );
    }
}