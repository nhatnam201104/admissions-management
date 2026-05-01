package com.example.thymeleaf_web.controller.user;

import com.example.thymeleaf_web.model.dto.request.UserCreateRequest;
import com.example.thymeleaf_web.model.dto.response.UserResponse;
import com.example.thymeleaf_web.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for user management operations.
 * Handles HTTP requests and returns Thymeleaf views.
 */
@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Display list of all users.
     */
    @GetMapping
    public String listUsers(Model model) {
        log.info("GET /users - Displaying user list");
        List<UserResponse> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/list";
    }
    
    /**
     * Display user creation form.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("GET /users/new - Displaying create form");
        model.addAttribute("user", new UserCreateRequest());
        return "user/form";
    }
    
    /**
     * Handle user creation.
     */
    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") UserCreateRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        log.info("POST /users - Creating new user: {}", request.getUsername());
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            return "user/form";
        }
        
        try {
            userService.createUser(request);
            redirectAttributes.addFlashAttribute("message", "User created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/users";
        } catch (Exception e) {
            log.error("Error creating user", e);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/users/new";
        }
    }
    
    /**
     * Display user details.
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        log.info("GET /users/{} - Viewing user details", id);
        UserResponse user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user/detail";
    }
    
    /**
     * Display user edit form.
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("GET /users/{}/edit - Displaying edit form", id);
        UserResponse user = userService.getUserById(id);
        
        // Convert UserResponse to UserCreateRequest for editing
        UserCreateRequest request = UserCreateRequest.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
        
        model.addAttribute("user", request);
        model.addAttribute("userId", id);
        return "user/form";
    }
    
    /**
     * Handle user update.
     */
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute("user") UserCreateRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        log.info("POST /users/{} - Updating user", id);
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            return "user/form";
        }
        
        try {
            userService.updateUser(id, request);
            redirectAttributes.addFlashAttribute("message", "User updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/users";
        } catch (Exception e) {
            log.error("Error updating user", e);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/users/" + id + "/edit";
        }
    }
    
    /**
     * Delete user.
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("POST /users/{}/delete - Deleting user", id);
        
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            log.error("Error deleting user", e);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        
        return "redirect:/users";
    }
    
    /**
     * Toggle user active status.
     */
    @PostMapping("/{id}/toggle")
    public String toggleUserActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("POST /users/{}/toggle - Toggling user status", id);
        
        try {
            userService.toggleUserActive(id);
            redirectAttributes.addFlashAttribute("message", "User status updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            log.error("Error toggling user status", e);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        
        return "redirect:/users";
    }
}