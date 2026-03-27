package com.buildsmart.vendor.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.vendor.entity.Vendor;
import com.buildsmart.vendor.dto.VendorRequest;
import com.buildsmart.vendor.dto.VendorResponse;
import com.buildsmart.vendor.repository.VendorRepository;
import com.buildsmart.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorResponse createVendor(VendorRequest request) {
        ApplicationLogger.log.info("Creating vendor");

        if (vendorRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Vendor already exists: " + request.name());
        }

        Vendor last = vendorRepository.findTopByOrderByVendorIdDesc();

        Vendor vendor = new Vendor();
        vendor.setVendorId(
                IdGeneratorUtil.nextVendorId(last == null ? null : last.getVendorId()));
        vendor.setName(request.name());
        vendor.setContactInfo(request.contactInfo());
        vendor.setStatus(request.status());

        return toResponse(vendorRepository.save(vendor));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getVendorsByStatus(String status) {
        return vendorRepository
                .findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private VendorResponse toResponse(Vendor vendor) {
        return VendorResponse.of(
                vendor.getVendorId(),
                vendor.getName(),
                vendor.getContactInfo(),
                vendor.getStatus()
        );
    }
}