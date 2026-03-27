package com.buildsmart.common.enums;

/**
 * Common Role Enum for the entire project.
 * Maps user roles to their corresponding departments and ID prefixes.
 */
public enum Role {
    ADMIN("ADMIN", "BSAD", null),
    PROJECT_MANAGER("PROJECT_MANAGER", "BSPM", Department.SITE),
    SITE_ENGINEER("SITE_ENGINEER", "BSSE", Department.SITE),
    SAFETY_OFFICER("SAFETY_OFFICER", "BSSO", Department.SAFETY),
    VENDOR("VENDOR", "BSVM", Department.VENDOR),
    FINANCE_OFFICER("FINANCE_OFFICER", "BSFO", Department.FINANCE);

    private final String roleName;
    private final String idPrefix;
    private final Department department;

    Role(String roleName, String idPrefix, Department department) {
        this.roleName = roleName;
        this.idPrefix = idPrefix;
        this.department = department;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public Department getDepartment() {
        return department;
    }

    /**
     * Get the role by department
     */
    public static Role getByDepartment(Department department) {
        if (department == null) {
            return null;
        }
        return switch (department) {
            case FINANCE -> FINANCE_OFFICER;
            case VENDOR -> VENDOR;
            case SAFETY -> SAFETY_OFFICER;
            case SITE -> SITE_ENGINEER;
        };
    }
}
