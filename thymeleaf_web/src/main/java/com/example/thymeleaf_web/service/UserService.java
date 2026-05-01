package com.example.thymeleaf_web.service;

import com.example.thymeleaf_web.model.dto.request.UserCreateRequest;
import com.example.thymeleaf_web.model.dto.response.UserResponse;
import com.example.thymeleaf_web.model.entity.User;

import java.util.List;

/**
 * Service interface for User business logic.
 */
public interface UserService {
    
    /**
     * Get all users.
     * @return List of user responses
     */
    List<UserResponse> getAllUsers();
    
    /**
     * Get user by ID.
     * @param id User ID
     * @return User response
     * @throws com.example.thymeleaf_web.exception.ResourceNotFoundException if user not found
     */
    UserResponse getUserById(Long id);
    
    /**
     * Create a new user.
     * @param request User creation request
     * @return Created user response
     */
    UserResponse createUser(UserCreateRequest request);
    
    /**
     * Update an existing user.
     * @param id User ID
     * @param request User update request
     * @return Updated user response
     * @throws com.example.thymeleaf_web.exception.ResourceNotFoundException if user not found
     */
    UserResponse updateUser(Long id, UserCreateRequest request);
    
    /**
     * Delete a user by ID.
     * @param id User ID
     * @throws com.example.thymeleaf_web.exception.ResourceNotFoundException if user not found
     */
    void deleteUser(Long id);
    
    /**
     * Toggle user active status.
     * @param id User ID
     * @throws com.example.thymeleaf_web.exception.ResourceNotFoundException if user not found
     */
    void toggleUserActive(Long id);
}