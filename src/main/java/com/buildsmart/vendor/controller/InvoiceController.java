package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.InvoiceRequest;
import com.buildsmart.vendor.dto.InvoiceResponse;
import com.buildsmart.vendor.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/invoices")
@RequiredArgsConstructor
@Tag(name = "Finance APIs", description = "Invoice management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create invoice")
    @ApiResponse(responseCode = "201", description = "Invoice created")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(request));
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Get invoices by contract ID")
    @ApiResponse(responseCode = "200", description = "Invoices fetched")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByContractId(
            @PathVariable String contractId) {
        return ResponseEntity
                .ok(invoiceService.getInvoicesByContractId(contractId));
    }
}