package com.sitemasterpro.controller;

import com.sitemasterpro.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Map<String, Object> sendMessage(@Payload Map<String, Object> chatMessage,
                                          SimpMessageHeaderAccessor headerAccessor,
                                          Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "MESSAGE");
            response.put("content", chatMessage.get("content"));
            response.put("sender", username);
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            logger.debug("WebSocket message sent by {}: {}", username, chatMessage.get("content"));
            return response;
        } catch (Exception e) {
            logger.error("Error handling WebSocket message: {}", e.getMessage());
            return null;
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Map<String, Object> addUser(@Payload Map<String, Object> chatMessage,
                                      SimpMessageHeaderAccessor headerAccessor,
                                      Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            
            // Add username in web socket session
            headerAccessor.getSessionAttributes().put("username", username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "JOIN");
            response.put("sender", username);
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            logger.info("User {} joined WebSocket session", username);
            return response;
        } catch (Exception e) {
            logger.error("Error adding user to WebSocket: {}", e.getMessage());
            return null;
        }
    }

    @MessageMapping("/dashboard.subscribe")
    public void subscribeToDashboard(Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            logger.debug("User {} subscribed to dashboard updates", username);
            
            // Send initial dashboard data
            notificationService.sendUserNotification(username, "Subscribed to dashboard updates");
        } catch (Exception e) {
            logger.error("Error subscribing to dashboard: {}", e.getMessage());
        }
    }

    @MessageMapping("/notifications.subscribe")
    public void subscribeToNotifications(Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            logger.debug("User {} subscribed to notifications", username);
            
            // Send welcome notification
            notificationService.sendUserNotification(username, "Connected to real-time notifications");
        } catch (Exception e) {
            logger.error("Error subscribing to notifications: {}", e.getMessage());
        }
    }

    @MessageMapping("/project.subscribe")
    public void subscribeToProjectUpdates(@Payload Map<String, Object> payload, Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            Long projectId = payload.get("projectId") != null ? 
                    Long.valueOf(payload.get("projectId").toString()) : null;
            
            logger.debug("User {} subscribed to project {} updates", username, projectId);
            
            if (projectId != null) {
                notificationService.sendUserNotification(username, 
                        "Subscribed to updates for project ID: " + projectId);
            }
        } catch (Exception e) {
            logger.error("Error subscribing to project updates: {}", e.getMessage());
        }
    }

    @MessageMapping("/inventory.subscribe")
    public void subscribeToInventoryUpdates(Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            logger.debug("User {} subscribed to inventory updates", username);
            
            notificationService.sendUserNotification(username, "Subscribed to inventory updates");
        } catch (Exception e) {
            logger.error("Error subscribing to inventory updates: {}", e.getMessage());
        }
    }

    @MessageMapping("/financial.subscribe")
    public void subscribeToFinancialUpdates(Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            logger.debug("User {} subscribed to financial updates", username);
            
            notificationService.sendUserNotification(username, "Subscribed to financial updates");
        } catch (Exception e) {
            logger.error("Error subscribing to financial updates: {}", e.getMessage());
        }
    }

    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public Map<String, Object> ping(@Payload Map<String, Object> message, Principal principal) {
        try {
            String username = principal != null ? principal.getName() : "Anonymous";
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "PONG");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("user", username);
            
            return response;
        } catch (Exception e) {
            logger.error("Error handling ping: {}", e.getMessage());
            return null;
        }
    }
}
