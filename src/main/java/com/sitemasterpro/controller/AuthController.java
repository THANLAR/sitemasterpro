package com.sitemasterpro.controller;

import com.sitemasterpro.dto.LoginRequest;
import com.sitemasterpro.dto.LoginResponse;
import com.sitemasterpro.entity.Role;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                         HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        LoginResponse response = authService.authenticateUser(loginRequest, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest signUpRequest,
                                         HttpServletRequest request) {
        try {
            User user = authService.registerUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getFullName(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getRoles()
            );
            
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            String newToken = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(new TokenRefreshResponse(newToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Refresh token is not valid!"));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@RequestParam String username, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        authService.logout(username, ipAddress);
        return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // DTOs for request/response
    public static class UserRegistrationRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;
        private String phoneNumber;
        private Set<Role.RoleName> roles;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public Set<Role.RoleName> getRoles() { return roles; }
        public void setRoles(Set<Role.RoleName> roles) { this.roles = roles; }
    }

    public static class TokenRefreshRequest {
        private String refreshToken;
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class TokenRefreshResponse {
        private String accessToken;
        
        public TokenRefreshResponse(String accessToken) {
            this.accessToken = accessToken;
        }
        
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    }

    public static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
