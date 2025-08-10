package com.sitemasterpro.enums;

public enum TransactionType {
    STOCK_IN("Stock In", "Material received from supplier"),
    STOCK_OUT("Stock Out", "Material issued to project/team"),
    ADJUSTMENT("Adjustment", "Inventory adjustment for corrections"),
    DAMAGE("Damage", "Material damaged or unusable"),
    RETURN("Return", "Material returned to supplier");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
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
