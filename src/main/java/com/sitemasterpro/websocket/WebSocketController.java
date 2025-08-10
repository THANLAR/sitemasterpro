package com.sitemasterpro.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Map<String, Object> greeting(Map<String, String> message) {
        logger.debug("Received WebSocket message: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", "Hello, " + message.get("name") + "!");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    @SubscribeMapping("/topic/alerts")
    public Map<String, Object> subscribeToAlerts() {
        logger.info("Client subscribed to alerts");
        
        Map<String, Object> welcomeMessage = new HashMap<>();
        welcomeMessage.put("type", "SUBSCRIPTION_CONFIRMED");
        welcomeMessage.put("message", "Successfully subscribed to alerts");
        welcomeMessage.put("timestamp", LocalDateTime.now());
        
        return welcomeMessage;
    }

    @SubscribeMapping("/topic/project-updates")
    public Map<String, Object> subscribeToProjectUpdates() {
        logger.info("Client subscribed to project updates");
        
        Map<String, Object> welcomeMessage = new HashMap<>();
        welcomeMessage.put("type", "SUBSCRIPTION_CONFIRMED");
        welcomeMessage.put("message", "Successfully subscribed to project updates");
        welcomeMessage.put("timestamp", LocalDateTime.now());
        
        return welcomeMessage;
    }

    @SubscribeMapping("/topic/financial-updates")
    public Map<String, Object> subscribeToFinancialUpdates() {
        logger.info("Client subscribed to financial updates");
        
        Map<String, Object> welcomeMessage = new HashMap<>();
        welcomeMessage.put("type", "SUBSCRIPTION_CONFIRMED");
        welcomeMessage.put("message", "Successfully subscribed to financial updates");
        welcomeMessage.put("timestamp", LocalDateTime.now());
        
        return welcomeMessage;
    }

    @SubscribeMapping("/topic/inventory-updates")
    public Map<String, Object> subscribeToInventoryUpdates() {
        logger.info("Client subscribed to inventory updates");
        
        Map<String, Object> welcomeMessage = new HashMap<>();
        welcomeMessage.put("type", "SUBSCRIPTION_CONFIRMED");
        welcomeMessage.put("message", "Successfully subscribed to inventory updates");
        welcomeMessage.put("timestamp", LocalDateTime.now());
        
        return welcomeMessage;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Map<String, Object> chat(Map<String, String> message) {
        logger.debug("Chat message received: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", message.get("user"));
        response.put("message", message.get("message"));
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public Map<String, Object> notification(Map<String, String> message) {
        logger.info("Notification sent: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "USER_NOTIFICATION");
        response.put("title", message.get("title"));
        response.put("message", message.get("message"));
        response.put("sender", message.get("sender"));
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
}
