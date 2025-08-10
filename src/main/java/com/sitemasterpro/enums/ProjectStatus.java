package com.sitemasterpro.enums;

public enum ProjectStatus {
    PLANNING("Planning", "Project in planning phase"),
    ACTIVE("Active", "Project is currently active"),
    ON_HOLD("On Hold", "Project temporarily paused"),
    COMPLETED("Completed", "Project successfully completed"),
    CANCELLED("Cancelled", "Project has been cancelled");

    private final String displayName;
    private final String description;

    ProjectStatus(String displayName, String description) {
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
