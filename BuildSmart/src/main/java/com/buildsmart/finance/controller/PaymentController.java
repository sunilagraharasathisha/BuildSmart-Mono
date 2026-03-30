package com.buildsmart.finance.controller;

import com.buildsmart.finance.dto.PaymentRequest;
import com.buildsmart.finance.dto.PaymentResponse;
import com.buildsmart.finance.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/payments")
@RequiredArgsConstructor
@Tag(name = "Finance APIs", description = "Payment management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_OFFICER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create payment")
    @ApiResponse(responseCode = "201", description = "Payment created")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @GetMapping("/invoice/{invoiceId}/amount")
    @Operation(summary = "Get invoice amount by invoice ID")
    @ApiResponse(responseCode = "200", description = "Invoice amount fetched")
    public ResponseEntity<java.math.BigDecimal> getInvoiceAmount(@PathVariable String invoiceId) {
        return ResponseEntity.ok(paymentService.getInvoiceAmount(invoiceId));
    }
}
