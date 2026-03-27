package com.buildsmart.siteops.enums;

/**
 * Lifecycle of a Site Engineer's resource request.
 *
 * Workflow:
 *   PENDING  → APPROVED  (PM approves → Allocation is auto-created as Active)
 *   PENDING  → REJECTED  (PM rejects with a reason)
 *   APPROVED → RELEASED  (PM marks work done; underlying allocation is Released)
 *   PENDING  → CANCELLED (SE withdraws the request before PM acts)
 */
public enum ResourceRequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    RELEASED,
    CANCELLED
}
