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
import lombok.experimental.NonFinal;
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

    @NonFinal
    private UserDTO currentUser = new UserDTO();

    @Override
    public LoginResponseDTO login(@Valid LoginDTO loginDTO) {
        var userOptional = userRepository.findByUsername(loginDTO.getUsername());

        if (userOptional.isEmpty()) {
            throw new AuthenticationException("Invalid username or password.");
        }

        var user = userOptional.get();

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        UserDTO userDTO = userMapper.toUserDTO(user);

        // Store current user in session
        this.currentUser = userDTO;

        return LoginResponseDTO.success(userDTO);
    }

    @Override
    public UserDTO getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(UserDTO user) {
        this.currentUser = user;
    }

    @Override
    public Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
}
