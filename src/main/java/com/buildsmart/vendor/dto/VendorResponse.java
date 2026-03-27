package com.buildsmart.vendor.dto;

public record VendorResponse(
        String vendorId,
        String name,
        String contactInfo,
        String status,
        boolean active
) {
    public static VendorResponse of(
            String vendorId,
            String name,
            String contactInfo,
            String status
    ) {
        boolean active = "ACTIVE".equalsIgnoreCase(status);
        return new VendorResponse(vendorId, name, contactInfo, status, active);
    }
}
