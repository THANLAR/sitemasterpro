package com.sitemasterpro.dto;

import com.sitemasterpro.entity.FinancialTransaction;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancialTransactionDto {
    private Long id;

    @NotNull
    private Long projectId;

    @NotNull
    private FinancialTransaction.TransactionType type;

    @NotNull
    private FinancialTransaction.Category category;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String description;

    private String referenceNumber;
    private String notes;
    private LocalDateTime transactionDate;
    private Long createdById;

    // Constructors
    public FinancialTransactionDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public FinancialTransaction.TransactionType getType() {
        return type;
    }

    public void setType(FinancialTransaction.TransactionType type) {
        this.type = type;
    }

    public FinancialTransaction.Category getCategory() {
        return category;
    }

    public void setCategory(FinancialTransaction.Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
}
