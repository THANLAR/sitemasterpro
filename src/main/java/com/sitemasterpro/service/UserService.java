package com.sitemasterpro.service;

import com.sitemasterpro.entity.Role;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.exception.CustomException;
import com.sitemasterpro.repository.RoleRepository;
import com.sitemasterpro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return com.sitemasterpro.security.UserPrincipal.create(user);
    }

    public User createUser(String username, String email, String password, String fullName, 
                          Set<Role.RoleName> roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new CustomException("Email is already in use!");
        }

        User user = new User(username, email, passwordEncoder.encode(password), fullName);

        // Assign roles
        for (Role.RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new CustomException("Role not found: " + roleName));
            user.getRoles().add(role);
        }

        User savedUser = userRepository.save(user);
        logger.info("User created: {}", savedUser.getUsername());
        
        auditService.logAction("CREATE_USER", "User", savedUser.getId(), 
                              null, "User created: " + savedUser.getUsername());

        return savedUser;
    }

    public User updateUser(Long userId, String fullName, String email, String phoneNumber) {
        User user = getUserById(userId);
        
        String oldValues = String.format("fullName: %s, email: %s, phoneNumber: %s", 
                                        user.getFullName(), user.getEmail(), user.getPhoneNumber());

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);

        User updatedUser = userRepository.save(user);
        
        String newValues = String.format("fullName: %s, email: %s, phoneNumber: %s", 
                                        updatedUser.getFullName(), updatedUser.getEmail(), updatedUser.getPhoneNumber());
        
        auditService.logAction("UPDATE_USER", "User", userId, oldValues, newValues);
        
        return updatedUser;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findAllActiveUsers();
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    public void updateLastLoginTime(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
        
        auditService.logAction("TOGGLE_USER_STATUS", "User", userId, 
                              String.valueOf(!user.getEnabled()), String.valueOf(user.getEnabled()));
        
        logger.info("User status toggled for user: {}, new status: {}", 
                   user.getUsername(), user.getEnabled() ? "ENABLED" : "DISABLED");
    }

    public void changePassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        auditService.logAction("CHANGE_PASSWORD", "User", userId, null, "Password changed");
        
        logger.info("Password changed for user: {}", user.getUsername());
    }

    public void assignRoles(Long userId, Set<Role.RoleName> roleNames) {
        User user = getUserById(userId);
        
        String oldRoles = user.getRoles().toString();
        user.getRoles().clear();
        
        for (Role.RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new CustomException("Role not found: " + roleName));
            user.getRoles().add(role);
        }
        
        userRepository.save(user);
        
        auditService.logAction("ASSIGN_ROLES", "User", userId, oldRoles, user.getRoles().toString());
        
        logger.info("Roles assigned to user {}: {}", user.getUsername(), roleNames);
    }
}
