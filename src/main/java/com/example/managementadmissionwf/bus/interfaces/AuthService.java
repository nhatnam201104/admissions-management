package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.Auth.LoginDTO;
import com.example.managementadmissionwf.dto.Auth.LoginResponseDTO;

/**
 * Authentication business service interface
 */
public interface AuthService {
    /**
     * Authenticate user with username and password
     * @param loginDTO Login credentials
     * @return LoginResponseDTO containing authentication result and user profile
     * @throws com.example.managementadmissionwf.exception.AuthenticationException if credentials are invalid
     */
    LoginResponseDTO login(LoginDTO loginDTO);
}
