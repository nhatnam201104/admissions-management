package com.example.thymeleaf_web.service.impl;

import com.example.thymeleaf_web.exception.BusinessException;
import com.example.thymeleaf_web.exception.ResourceNotFoundException;
import com.example.thymeleaf_web.model.dto.request.UserCreateRequest;
import com.example.thymeleaf_web.model.dto.response.UserResponse;
import com.example.thymeleaf_web.model.entity.User;
import com.example.thymeleaf_web.repository.UserRepository;
import com.example.thymeleaf_web.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService.
 * Contains business logic for user operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return UserResponse.fromEntity(user);
    }
    
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Email already exists: " + request.getEmail());
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        
        return UserResponse.fromEntity(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserCreateRequest request) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        // Check if username is being changed and already exists
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists: " + request.getUsername());
        }
        
        // Check if email is being changed and already exists
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Email already exists: " + request.getEmail());
        }
        
        // Update user fields
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : user.getRole());
        
        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        
        return UserResponse.fromEntity(updatedUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }
    
    @Override
    @Transactional
    public void toggleUserActive(Long id) {
        log.info("Toggling active status for user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        user.setActive(!user.getActive());
        User updatedUser = userRepository.save(user);
        
        log.info("User active status toggled. User id: {}, active: {}", id, updatedUser.getActive());
    }
}