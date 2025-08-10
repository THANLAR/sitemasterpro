package com.sitemasterpro.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitemasterpro.entity.AuditLog;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuditUtil {

    private static final Logger logger = LoggerFactory.getLogger(AuditUtil.class);

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    public void logAction(User user, String action, String entityType, Long entityId) {
        logAction(user, action, entityType, entityId, null, null, null);
    }

    public void logAction(User user, String action, String entityType, Long entityId, 
                         Object oldValue, Object newValue, HttpServletRequest request) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(user);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);

            if (oldValue != null) {
                auditLog.setOldValue(objectMapper.writeValueAsString(oldValue));
            }

            if (newValue != null) {
                auditLog.setNewValue(objectMapper.writeValueAsString(newValue));
            }

            if (request != null) {
                auditLog.setIpAddress(getClientIpAddress(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
            }

            auditService.saveAuditLog(auditLog);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing audit log data: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error saving audit log: {}", e.getMessage());
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}
