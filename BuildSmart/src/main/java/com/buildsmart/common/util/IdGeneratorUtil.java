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
}
