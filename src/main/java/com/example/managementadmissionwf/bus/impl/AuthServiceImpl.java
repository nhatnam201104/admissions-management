package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AuthService;
import com.example.managementadmissionwf.dal.repository.UserRepository;
import com.example.managementadmissionwf.dto.Auth.LoginDTO;
import com.example.managementadmissionwf.dto.Auth.LoginResponseDTO;
import com.example.managementadmissionwf.dto.User.UserDTO;
import com.example.managementadmissionwf.exception.AuthenticationException;
import com.example.managementadmissionwf.mapper.UserMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service implementation
 * Handles all authentication business logic
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(@Valid LoginDTO loginDTO) {
        // Step 1: Find user by username
        var userOptional = userRepository.findByUsername(loginDTO.getUsername());
        
        // Step 2: Validate credentials
        if (userOptional.isEmpty()) {
            // Use same message for security (prevent username enumeration)
            throw new AuthenticationException("Invalid username or password.");
        }
        
        var user = userOptional.get();
        
        // Step 3: Verify password using encoder
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }
        
        // Step 4: Map to DTO and return
        UserDTO userDTO = userMapper.toUserDTO(user);
        
        return LoginResponseDTO.success(userDTO);
    }
}
