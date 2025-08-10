package com.sitemasterpro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MaterialDto {
    private Long id;

    @NotBlank(message = "Material name is required")
    private String name;

    private String description;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "Current stock cannot be negative")
    private Double currentStock;

    @PositiveOrZero(message = "Minimum stock cannot be negative")
    private Double minimumStock;

    @PositiveOrZero(message = "Maximum stock cannot be negative")
    private Double maximumStock;

    private Double reorderPoint;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long supplierId;
    private String supplierName;

    // Constructors
    public MaterialDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Double getCurrentStock() { return currentStock; }
    public void setCurrentStock(Double currentStock) { this.currentStock = currentStock; }

    public Double getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Double minimumStock) { this.minimumStock = minimumStock; }

    public Double getMaximumStock() { return maximumStock; }
    public void setMaximumStock(Double maximumStock) { this.maximumStock = maximumStock; }

    public Double getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Double reorderPoint) { this.reorderPoint = reorderPoint; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
