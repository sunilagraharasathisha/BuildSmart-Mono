package com.buildsmart.finance.service;

import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceIdGeneratorService {

    private static final String PREFIX = "INVBS";

    private final InvoiceRepository invoiceRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    public InvoiceIdGeneratorService(InvoiceRepository invoiceRepository, IdGeneratorUtil idGeneratorUtil) {
        this.invoiceRepository = invoiceRepository;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Transactional(readOnly = true)
    public String generateNextInvoiceId() {
        long count = invoiceRepository.countByInvoiceIdStartingWith(PREFIX);
        return idGeneratorUtil.generateId(PREFIX, count + 1);
    }
}
