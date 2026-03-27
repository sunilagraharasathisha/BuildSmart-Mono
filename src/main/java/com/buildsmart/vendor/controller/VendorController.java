package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.VendorRequest;
import com.buildsmart.vendor.dto.VendorResponse;
import com.buildsmart.vendor.service.VendorService;
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
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor APIs", description = "Vendor management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT_OFFICER')")
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @Operation(summary = "Create vendor")
    @ApiResponse(responseCode = "201", description = "Vendor created")
    public ResponseEntity<VendorResponse> createVendor(
            @Valid @RequestBody VendorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vendorService.createVendor(request));
    }

    @GetMapping
    @Operation(summary = "Get vendors by status")
    @ApiResponse(responseCode = "200", description = "Vendors fetched")
    public ResponseEntity<List<VendorResponse>> getVendorsByStatus(
            @RequestParam String status) {
        return ResponseEntity
                .ok(vendorService.getVendorsByStatus(status));
    }
}