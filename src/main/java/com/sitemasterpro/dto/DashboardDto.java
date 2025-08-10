package com.sitemasterpro.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDto {
    private int totalProjects;
    private int activeProjects;
    private int completedProjects;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal overallProfitMargin;
    private int lowStockItems;
    private int pendingApprovals;
    private int overdueProjects;
    private List<ProjectSummary> recentProjects;
    private Map<String, BigDecimal> monthlyRevenue;
    private Map<String, BigDecimal> expensesByCategory;

    // Constructors
    public DashboardDto() {}

    // Getters and Setters
    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

    public int getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(int activeProjects) {
        this.activeProjects = activeProjects;
    }

    public int getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(int completedProjects) {
        this.completedProjects = completedProjects;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getOverallProfitMargin() {
        return overallProfitMargin;
    }

    public void setOverallProfitMargin(BigDecimal overallProfitMargin) {
        this.overallProfitMargin = overallProfitMargin;
    }

    public int getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(int lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public int getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(int pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public int getOverdueProjects() {
        return overdueProjects;
    }

    public void setOverdueProjects(int overdueProjects) {
        this.overdueProjects = overdueProjects;
    }

    public List<ProjectSummary> getRecentProjects() {
        return recentProjects;
    }

    public void setRecentProjects(List<ProjectSummary> recentProjects) {
        this.recentProjects = recentProjects;
    }

    public Map<String, BigDecimal> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Map<String, BigDecimal> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Map<String, BigDecimal> getExpensesByCategory() {
        return expensesByCategory;
    }

    public void setExpensesByCategory(Map<String, BigDecimal> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    // Inner class for project summaries
    public static class ProjectSummary {
        private Long id;
        private String name;
        private String location;
        private BigDecimal completionPercentage;
        private String status;
        private BigDecimal profitMargin;

        // Constructors
        public ProjectSummary() {}

        public ProjectSummary(Long id, String name, String location, BigDecimal completionPercentage, String status, BigDecimal profitMargin) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.completionPercentage = completionPercentage;
            this.status = status;
            this.profitMargin = profitMargin;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public BigDecimal getCompletionPercentage() { return completionPercentage; }
        public void setCompletionPercentage(BigDecimal completionPercentage) { this.completionPercentage = completionPercentage; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public BigDecimal getProfitMargin() { return profitMargin; }
        public void setProfitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; }
    }
}
