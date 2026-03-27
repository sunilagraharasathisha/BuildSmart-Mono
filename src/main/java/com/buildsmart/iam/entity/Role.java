package com.buildsmart.iam.entity;

/**
 * This Role enum is now deprecated. Use com.buildsmart.common.enums.Role instead.
 * @deprecated Use com.buildsmart.common.enums.Role
 */
@Deprecated(since = "1.1", forRemoval = true)
public enum Role {
    ADMIN,
    PROJECT_MANAGER,
    SITE_ENGINEER,
    SAFETY_OFFICER,
    VENDOR,
    FINANCE_OFFICER
}
