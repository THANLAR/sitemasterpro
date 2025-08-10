package com.sitemasterpro.enums;

public enum UserRole {
    SUPER_ADMIN("Super Admin", "Full system access, user management"),
    ADMIN("Admin", "Multi-project oversight, mid-level approvals"),
    CEO("CEO", "Executive dashboards, strategic decisions"),
    ACCOUNTANT("Accountant", "Financial management, budget tracking"),
    STORE_KEEPER("Store Keeper", "Inventory management, material tracking"),
    SITE_MANAGER("Site Manager", "On-site operations, progress updates"),
    SITE_ENGINEER("Site Engineer", "Technical execution, quality control"),
    LABOR_HEAD("Labor Head", "Workforce management, attendance tracking");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
