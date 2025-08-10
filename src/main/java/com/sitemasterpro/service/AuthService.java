package com.sitemasterpro.service;

import com.sitemasterpro.dto.LoginRequest;
import com.sitemasterpro.dto.LoginResponse;
import com.sitemasterpro.entity.Role;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.exception.CustomException;
import com.sitemasterpro.repository.RoleRepository;
import com.sitemasterpro.repository.UserRepository;
import com.sitemasterpro.security.JwtUtil;
import com.sitemasterpro.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuditService auditService;

    public LoginResponse authenticateUser(LoginRequest loginRequest, String ipAddress) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtUtil.generateJwtToken(userPrincipal);
            String refreshToken = jwtUtil.generateRefreshToken(userPrincipal.getUsername());

            // Update last login time
            User user = userRepository.findById(userPrincipal.getId()).orElse(null);
            if (user != null) {
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
            }

            // Log successful login
            auditService.logLoginAttempt(loginRequest.getUsername(), true, ipAddress);

            Set<String> roles = userPrincipal.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet());

            logger.info("User {} authenticated successfully", loginRequest.getUsername());

            return new LoginResponse(jwt, refreshToken, userPrincipal.getId(), 
                                   userPrincipal.getUsername(), userPrincipal.getEmail(), roles);

        } catch (Exception e) {
            auditService.logLoginAttempt(loginRequest.getUsername(), false, ipAddress);
            logger.warn("Authentication failed for user: {}", loginRequest.getUsername());
            throw new CustomException("Invalid username or password");
        }
    }

    public User registerUser(String username, String email, String password, String fullName, 
                           String phoneNumber, Set<Role.RoleName> roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new CustomException("Error: Email is already in use!");
        }

        // Create new user account
        User user = new User(username, email, passwordEncoder.encode(password), fullName);
        user.setPhoneNumber(phoneNumber);

        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_SITE_ENGINEER)
                    .orElseThrow(() -> new CustomException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            roleNames.forEach(role -> {
                Role userRole = roleRepository.findByName(role)
                        .orElseThrow(() -> new CustomException("Error: Role " + role + " is not found."));
                roles.add(userRole);
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        auditService.logAction("REGISTER_USER", "User", savedUser.getId(), 
                              null, "User registered: " + savedUser.getUsername());

        logger.info("New user registered: {}", savedUser.getUsername());
        return savedUser;
    }

    public String refreshToken(String refreshToken) {
        if (jwtUtil.validateJwtToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromJwtToken(refreshToken);
            return jwtUtil.generateTokenFromUsername(username);
        }
        throw new CustomException("Invalid refresh token");
    }

    public void logout(String username, String ipAddress) {
        auditService.logLogout(username, ipAddress);
        SecurityContextHolder.clearContext();
        logger.info("User {} logged out", username);
    }

    public UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }

    public boolean hasRole(String roleName) {
        UserPrincipal currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
        }
        return false;
    }

    public boolean canAccessProject(Long projectId) {
        UserPrincipal currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Super Admin and Admin can access all projects
        if (hasRole("SUPER_ADMIN") || hasRole("ADMIN") || hasRole("CEO")) {
            return true;
        }

        // Other roles can only access projects they are assigned to
        User user = userRepository.findById(currentUser.getId()).orElse(null);
        if (user != null) {
            return user.getProjects().stream()
                    .anyMatch(project -> project.getId().equals(projectId));
        }
        
        return false;
    }
}
package com.sitemasterpro.service;

import com.sitemasterpro.dto.LoginRequest;
import com.sitemasterpro.dto.LoginResponse;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.entity.Role;
import com.sitemasterpro.repository.UserRepository;
import com.sitemasterpro.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticateUser(LoginRequest loginRequest, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login
        user.setLastLoginDate(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new LoginResponse(token, refreshToken, user.getUsername(), user.getEmail(), 
                               user.getRoles().stream().map(Role::getName).toList());
    }

    public User registerUser(String username, String email, String password, 
                           String fullName, String phoneNumber, Set<String> roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setEnabled(true);
        user.setCreatedDate(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_SITE_ENGINEER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(userRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
}
