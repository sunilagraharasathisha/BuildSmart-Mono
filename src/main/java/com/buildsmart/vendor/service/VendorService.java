package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.VendorRequest;
import com.buildsmart.vendor.dto.VendorResponse;

import java.util.List;

public interface VendorService {

    VendorResponse createVendor(VendorRequest request);

    List<VendorResponse> getVendorsByStatus(String status);
}