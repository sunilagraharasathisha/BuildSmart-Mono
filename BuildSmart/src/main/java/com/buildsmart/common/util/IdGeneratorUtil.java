package com.buildsmart.common.util;

import com.buildsmart.common.enums.Department;

import java.time.LocalDate;

public final class IdGeneratorUtil {

    private IdGeneratorUtil() {
    }

    public static String nextProjectId(String lastProjectId) {
        int year = LocalDate.now().getYear() % 100;
        int next = extractNumericSuffix(lastProjectId, 3) + 1;
        return String.format("CHEBS%02d%03d", year, next);
    }

    public static String nextTaskId(Department department, String lastTaskId) {
        int next = extractNumericSuffix(lastTaskId, 3) + 1;
        return switch (department) {
            case FINANCE -> String.format("FINBS%03d", next);
            case VENDOR -> String.format("VENBS%03d", next);
            case SAFETY -> String.format("SAFBS%03d", next);
            case SITE -> String.format("SITBS%03d", next);
        };
    }

    public static String nextBudgetId(String lastBudgetId) {
        int next = extractNumericSuffix(lastBudgetId, 3) + 1;
        return String.format("BUDBS%03d", next);
    }

    public static String nextExpenseId(String lastExpenseId) {
        int next = extractNumericSuffix(lastExpenseId, 3) + 1;
        return String.format("EXPBS%03d", next);
    }

    public static String nextPaymentId(String lastPaymentId) {
        int next = extractNumericSuffix(lastPaymentId, 3) + 1;
        return String.format("PAYBS%03d", next);
    }

    public static String nextNotificationId(String lastNotificationId) {
        int next = extractNumericSuffix(lastNotificationId, 3) + 1;
        return String.format("NOTBS%03d", next);
    }

    public static String nextIncidentId(String lastIncidentId) {
        int next = extractNumericSuffix(lastIncidentId, 3) + 1;
        return String.format("INCBS%03d", next);
    }

    public static String nextInspectionId(String lastInspectionId) {
        int next = extractNumericSuffix(lastInspectionId, 3) + 1;
        return String.format("INSBS%03d", next);
    }

    private static int extractNumericSuffix(String id, int digits) {
        if (id == null || id.length() < digits) {
            return 0;
        }
        String suffix = id.substring(id.length() - digits);
        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static String nextVendorId(String lastVendorId) {
        int next = extractNumericSuffix(lastVendorId, 3) + 1;
        return String.format("VENBS%03d", next);
    }

    public static String nextContractId(String lastContractId) {
        int next = extractNumericSuffix(lastContractId, 3) + 1;
        return String.format("CONBS%03d", next);
    }

    public static String nextDeliveryId(String lastDeliveryId) {
        int next = extractNumericSuffix(lastDeliveryId, 3) + 1;
        return String.format("DELBS%03d", next);
    }

    public static String nextInvoiceId(String lastInvoiceId) {
        int next = extractNumericSuffix(lastInvoiceId, 3) + 1;
        return String.format("INVBS%03d", next);

    }

    public static String nextResourceId(String lastResourceId) {
        int next = extractNumericSuffix(lastResourceId, 3) + 1;
        return String.format("RESBS%03d", next);
    }

    /** e.g. ALCBS001, ALCBS002 */
    public static String nextAllocationId(String lastAllocationId) {
        int next = extractNumericSuffix(lastAllocationId, 3) + 1;
        return String.format("ALCBS%03d", next);
    }

    /* ─── New generators for SiteOps module ─── */

    /** e.g. LOGBS001, LOGBS002 */
    public static String nextSiteLogId(String lastSiteLogId) {
        int next = extractNumericSuffix(lastSiteLogId, 3) + 1;
        return String.format("LOGBS%03d", next);
    }

    /** e.g. ISSBS001, ISSBS002 */
    public static String nextIssueId(String lastIssueId) {
        int next = extractNumericSuffix(lastIssueId, 3) + 1;
        return String.format("ISSBS%03d", next);
    }

    /** e.g. RREBS001, RREBS002 — SiteOps ResourceRequest IDs */
    public static String nextResourceRequestId(String lastResourceRequestId) {
        int next = extractNumericSuffix(lastResourceRequestId, 3) + 1;
        return String.format("RREBS%03d", next);
    }


}

