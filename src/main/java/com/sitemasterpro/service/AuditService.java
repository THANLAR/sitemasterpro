package com.sitemasterpro.service;

import com.sitemasterpro.entity.AuditLog;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String action, String entityType, Long entityId, String oldValues, String newValues) {
        try {
            User currentUser = getCurrentUser();
            String ipAddress = getCurrentIpAddress();
            String userAgent = getCurrentUserAgent();

            AuditLog auditLog = new AuditLog(currentUser, action, entityType, entityId, 
                                           oldValues, newValues, ipAddress);
            auditLog.setUserAgent(userAgent);

            auditLogRepository.save(auditLog);
            
            logger.debug("Audit log created: {} by user {} on {} {}", 
                        action, currentUser != null ? currentUser.getUsername() : "SYSTEM", 
                        entityType, entityId);
        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
            // Don't fail the main operation if audit logging fails
        }
    }

    public void logLoginAttempt(String username, boolean successful, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(successful ? "LOGIN_SUCCESS" : "LOGIN_FAILURE");
            auditLog.setEntityType("Authentication");
            auditLog.setNewValues("Username: " + username);
            auditLog.setIpAddress(ipAddress);
            auditLog.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            
            logger.info("Login attempt logged: {} for user {} from IP {}", 
                       successful ? "SUCCESS" : "FAILURE", username, ipAddress);
        } catch (Exception e) {
            logger.error("Failed to log login attempt", e);
        }
    }

    public void logLogout(String username, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction("LOGOUT");
            auditLog.setEntityType("Authentication");
            auditLog.setNewValues("Username: " + username);
            auditLog.setIpAddress(ipAddress);
            auditLog.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            
            logger.info("Logout logged for user {} from IP {}", username, ipAddress);
        } catch (Exception e) {
            logger.error("Failed to log logout", e);
        }
    }

    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByDateRange(startDate, endDate);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllOrderByTimestampDesc();
    }

    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof com.sitemasterpro.security.UserPrincipal) {
                    com.sitemasterpro.security.UserPrincipal userPrincipal = 
                            (com.sitemasterpro.security.UserPrincipal) principal;
                    User user = new User();
                    user.setId(userPrincipal.getId());
                    user.setUsername(userPrincipal.getUsername());
                    return user;
                }
            }
        } catch (Exception e) {
            logger.warn("Could not get current user for audit log", e);
        }
        return null;
    }

    private String getCurrentIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String getCurrentUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("User-Agent");
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
