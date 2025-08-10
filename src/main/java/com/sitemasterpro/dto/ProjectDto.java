package com.sitemasterpro.dto;

import com.sitemasterpro.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectDto {
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String location;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private BigDecimal contractValue;

    private BigDecimal budgetedCost;

    private BigDecimal completionPercentage;

    private Project.ProjectStatus status;

    // Constructors
    public ProjectDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getContractValue() {
        return contractValue;
    }

    public void setContractValue(BigDecimal contractValue) {
        this.contractValue = contractValue;
    }

    public BigDecimal getBudgetedCost() {
        return budgetedCost;
    }

    public void setBudgetedCost(BigDecimal budgetedCost) {
        this.budgetedCost = budgetedCost;
    }

    public BigDecimal getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(BigDecimal completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Project.ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(Project.ProjectStatus status) {
        this.status = status;
    }
}
