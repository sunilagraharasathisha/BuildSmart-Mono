package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.InvoiceRequest;
import com.buildsmart.vendor.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    List<InvoiceResponse> getInvoicesByContractId(String contractId);
}