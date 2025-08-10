package com.sitemasterpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    private String location;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate actualEndDate;

    @NotNull
    @Column(precision = 15, scale = 2)
    private BigDecimal contractValue;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal budgetedCost = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal actualCost = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal actualRevenue = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal completionPercentage = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.PLANNING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectMilestone> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FinancialTransaction> financialTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryTransaction> inventoryTransactions = new ArrayList<>();

    public Project() {}

    public Project(String name, String description, String location, LocalDate startDate, BigDecimal contractValue) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.contractValue = contractValue;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal calculateProfitMargin() {
        if (actualRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal profit = actualRevenue.subtract(actualCost);
        return profit.divide(actualRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }

    public BigDecimal getContractValue() { return contractValue; }
    public void setContractValue(BigDecimal contractValue) { this.contractValue = contractValue; }

    public BigDecimal getBudgetedCost() { return budgetedCost; }
    public void setBudgetedCost(BigDecimal budgetedCost) { this.budgetedCost = budgetedCost; }

    public BigDecimal getActualCost() { return actualCost; }
    public void setActualCost(BigDecimal actualCost) { this.actualCost = actualCost; }

    public BigDecimal getActualRevenue() { return actualRevenue; }
    public void setActualRevenue(BigDecimal actualRevenue) { this.actualRevenue = actualRevenue; }

    public BigDecimal getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(BigDecimal completionPercentage) { this.completionPercentage = completionPercentage; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ProjectMilestone> getMilestones() { return milestones; }
    public void setMilestones(List<ProjectMilestone> milestones) { this.milestones = milestones; }

    public List<FinancialTransaction> getFinancialTransactions() { return financialTransactions; }
    public void setFinancialTransactions(List<FinancialTransaction> financialTransactions) { this.financialTransactions = financialTransactions; }

    public List<InventoryTransaction> getInventoryTransactions() { return inventoryTransactions; }
    public void setInventoryTransactions(List<InventoryTransaction> inventoryTransactions) { this.inventoryTransactions = inventoryTransactions; }

    public enum ProjectStatus {
        PLANNING,
        IN_PROGRESS,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }
}
package com.sitemasterpro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal contractValue;

    @Column(precision = 19, scale = 2)
    private BigDecimal budgetedCost;

    @Column(precision = 19, scale = 2)
    private BigDecimal actualCost = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.PLANNING;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public enum ProjectStatus {
        PLANNING, ACTIVE, ON_HOLD, COMPLETED, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getContractValue() { return contractValue; }
    public void setContractValue(BigDecimal contractValue) { this.contractValue = contractValue; }

    public BigDecimal getBudgetedCost() { return budgetedCost; }
    public void setBudgetedCost(BigDecimal budgetedCost) { this.budgetedCost = budgetedCost; }

    public BigDecimal getActualCost() { return actualCost; }
    public void setActualCost(BigDecimal actualCost) { this.actualCost = actualCost; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
