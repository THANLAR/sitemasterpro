package com.sitemasterpro.service;

import com.sitemasterpro.entity.Material;
import com.sitemasterpro.entity.Project;
import com.sitemasterpro.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendLowStockAlert(Material material) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "LOW_STOCK_ALERT");
        notification.put("message", String.format("Low stock alert: %s (Current: %s %s, Minimum: %s %s)", 
                material.getName(), material.getCurrentStock(), material.getUnit(),
                material.getMinStockLevel(), material.getUnit()));
        notification.put("materialId", material.getId());
        notification.put("currentStock", material.getCurrentStock());
        notification.put("minStockLevel", material.getMinStockLevel());
        notification.put("timestamp", LocalDateTime.now());
        notification.put("severity", "WARNING");

        messagingTemplate.convertAndSend("/topic/alerts", notification);
        logger.warn("Low stock alert sent for material: {}", material.getName());
    }

    public void sendBudgetOverrunAlert(Project project, BigDecimal budgetVariance) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "BUDGET_OVERRUN_ALERT");
        notification.put("message", String.format("Budget overrun alert: Project %s is %s%% over budget", 
                project.getName(), budgetVariance));
        notification.put("projectId", project.getId());
        notification.put("projectName", project.getName());
        notification.put("budgetVariance", budgetVariance);
        notification.put("actualCost", project.getActualCost());
        notification.put("budgetedCost", project.getBudgetedCost());
        notification.put("timestamp", LocalDateTime.now());
        notification.put("severity", "CRITICAL");

        messagingTemplate.convertAndSend("/topic/alerts", notification);
        logger.error("Budget overrun alert sent for project: {}", project.getName());
    }

    public void sendMilestoneDelayAlert(String milestoneName, String projectName, Long projectId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "MILESTONE_DELAY_ALERT");
        notification.put("message", String.format("Milestone delay: %s in project %s is overdue", 
                milestoneName, projectName));
        notification.put("projectId", projectId);
        notification.put("projectName", projectName);
        notification.put("milestoneName", milestoneName);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("severity", "HIGH");

        messagingTemplate.convertAndSend("/topic/alerts", notification);
        logger.warn("Milestone delay alert sent for milestone: {} in project: {}", milestoneName, projectName);
    }

    public void sendProjectStatusUpdate(Project project, String statusChange) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "PROJECT_STATUS_UPDATE");
        notification.put("message", String.format("Project %s status changed: %s", 
                project.getName(), statusChange));
        notification.put("projectId", project.getId());
        notification.put("projectName", project.getName());
        notification.put("newStatus", project.getStatus());
        notification.put("completionPercentage", project.getCompletionPercentage());
        notification.put("timestamp", LocalDateTime.now());
        notification.put("severity", "INFO");

        messagingTemplate.convertAndSend("/topic/project-updates", notification);
        logger.info("Project status update sent for project: {}", project.getName());
    }

    public void sendFinancialAlert(String type, String message, Long projectId, BigDecimal amount) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "FINANCIAL_ALERT");
        notification.put("subType", type);
        notification.put("message", message);
        notification.put("projectId", projectId);
        notification.put("amount", amount);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("severity", "HIGH");

        messagingTemplate.convertAndSend("/topic/financial-alerts", notification);
        logger.info("Financial alert sent: {}", message);
    }

    public void sendUserNotification(Long userId, String title, String message, String type) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "USER_NOTIFICATION");
        notification.put("title", title);
        notification.put("message", message);
        notification.put("notificationType", type);
        notification.put("userId", userId);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("read", false);

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);
        logger.info("User notification sent to user ID {}: {}", userId, title);
    }

    public void sendBroadcastNotification(String title, String message, String type, String severity) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "BROADCAST_NOTIFICATION");
        notification.put("title", title);
        notification.put("message", message);
        notification.put("notificationType", type);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("severity", severity);

        messagingTemplate.convertAndSend("/topic/broadcasts", notification);
        logger.info("Broadcast notification sent: {}", title);
    }

    public void sendInventoryUpdate(String action, String materialName, BigDecimal quantity, String unit) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "INVENTORY_UPDATE");
        notification.put("action", action);
        notification.put("message", String.format("%s: %s %s of %s", action, quantity, unit, materialName));
        notification.put("materialName", materialName);
        notification.put("quantity", quantity);
        notification.put("unit", unit);
        notification.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/inventory-updates", notification);
        logger.debug("Inventory update sent: {} {} of {}", action, quantity, materialName);
    }

    public void notifyRoleBasedUsers(List<String> roles, String title, String message, String type) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ROLE_BASED_NOTIFICATION");
        notification.put("title", title);
        notification.put("message", message);
        notification.put("notificationType", type);
        notification.put("targetRoles", roles);
        notification.put("timestamp", LocalDateTime.now());

        for (String role : roles) {
            messagingTemplate.convertAndSend("/topic/role/" + role.toLowerCase(), notification);
        }
        
        logger.info("Role-based notification sent to roles {}: {}", roles, title);
    }
}
