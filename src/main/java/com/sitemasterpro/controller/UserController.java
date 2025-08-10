package com.sitemasterpro.controller;

import com.sitemasterpro.dto.UserDto;
import com.sitemasterpro.entity.Role;
import com.sitemasterpro.entity.User;
import com.sitemasterpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto.getFullName(), 
                                                 userDto.getEmail(), userDto.getPhoneNumber());
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/assign-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> assignRoles(@PathVariable Long id, @RequestBody Set<Role.RoleName> roles) {
        userService.assignRoles(id, roles);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-role/{roleName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        List<User> users = userService.getUsersByRole("ROLE_" + roleName.toUpperCase());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    @ResponseBody
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    // Thymeleaf views
    @GetMapping("/view")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String usersView(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.RoleName.values());
        return "users";
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        return dto;
    }
}
