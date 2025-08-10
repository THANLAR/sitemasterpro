package com.sitemasterpro.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDataDto {
    // Project Statistics
    private long totalProjects;
    private long activeProjects;
    private long completedProjects;
    private long overdueProjects;

    // Financial Overview
    private BigDecimal totalBudget;
    private BigDecimal totalActualCost;
    private BigDecimal totalProjectedRevenue;
    private BigDecimal totalActualRevenue;
    private BigDecimal totalProfitMargin;

    // Inventory Statistics
    private long totalMaterials;
    private long lowStockMaterials;
    private long materialsNeedingReorder;
    private BigDecimal totalInventoryValue;

    // Recent Activities
    private List<String> recentActivities;

    // Alerts and Notifications
    private List<String> alerts;
    private List<String> notifications;

    // User Statistics
    private long totalUsers;
    private long activeUsers;

    // Charts Data
    private List<ChartData> monthlyExpenseData;
    private List<ChartData> projectStatusData;
    private List<ChartData> materialConsumptionData;

    // Constructors
    public DashboardDataDto() {}

    // Getters and Setters
    public long getTotalProjects() { return totalProjects; }
    public void setTotalProjects(long totalProjects) { this.totalProjects = totalProjects; }

    public long getActiveProjects() { return activeProjects; }
    public void setActiveProjects(long activeProjects) { this.activeProjects = activeProjects; }

    public long getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(long completedProjects) { this.completedProjects = completedProjects; }

    public long getOverdueProjects() { return overdueProjects; }
    public void setOverdueProjects(long overdueProjects) { this.overdueProjects = overdueProjects; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public BigDecimal getTotalActualCost() { return totalActualCost; }
    public void setTotalActualCost(BigDecimal totalActualCost) { this.totalActualCost = totalActualCost; }

    public BigDecimal getTotalProjectedRevenue() { return totalProjectedRevenue; }
    public void setTotalProjectedRevenue(BigDecimal totalProjectedRevenue) { this.totalProjectedRevenue = totalProjectedRevenue; }

    public BigDecimal getTotalActualRevenue() { return totalActualRevenue; }
    public void setTotalActualRevenue(BigDecimal totalActualRevenue) { this.totalActualRevenue = totalActualRevenue; }

    public BigDecimal getTotalProfitMargin() { return totalProfitMargin; }
    public void setTotalProfitMargin(BigDecimal totalProfitMargin) { this.totalProfitMargin = totalProfitMargin; }

    public long getTotalMaterials() { return totalMaterials; }
    public void setTotalMaterials(long totalMaterials) { this.totalMaterials = totalMaterials; }

    public long getLowStockMaterials() { return lowStockMaterials; }
    public void setLowStockMaterials(long lowStockMaterials) { this.lowStockMaterials = lowStockMaterials; }

    public long getMaterialsNeedingReorder() { return materialsNeedingReorder; }
    public void setMaterialsNeedingReorder(long materialsNeedingReorder) { this.materialsNeedingReorder = materialsNeedingReorder; }

    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }

    public List<String> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<String> recentActivities) { this.recentActivities = recentActivities; }

    public List<String> getAlerts() { return alerts; }
    public void setAlerts(List<String> alerts) { this.alerts = alerts; }

    public List<String> getNotifications() { return notifications; }
    public void setNotifications(List<String> notifications) { this.notifications = notifications; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

    public List<ChartData> getMonthlyExpenseData() { return monthlyExpenseData; }
    public void setMonthlyExpenseData(List<ChartData> monthlyExpenseData) { this.monthlyExpenseData = monthlyExpenseData; }

    public List<ChartData> getProjectStatusData() { return projectStatusData; }
    public void setProjectStatusData(List<ChartData> projectStatusData) { this.projectStatusData = projectStatusData; }

    public List<ChartData> getMaterialConsumptionData() { return materialConsumptionData; }
    public void setMaterialConsumptionData(List<ChartData> materialConsumptionData) { this.materialConsumptionData = materialConsumptionData; }

    // Inner class for chart data
    public static class ChartData {
        private String label;
        private BigDecimal value;

        public ChartData() {}

        public ChartData(String label, BigDecimal value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }
}
