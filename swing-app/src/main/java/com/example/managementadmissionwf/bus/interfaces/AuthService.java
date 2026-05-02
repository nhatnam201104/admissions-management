package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.Auth.LoginDTO;
import com.example.managementadmissionwf.dto.Auth.LoginResponseDTO;
import com.example.managementadmissionwf.dto.User.UserDTO;

/**
 * Authentication business service interface
 */
public interface AuthService {
    /**
     * Authenticate user with username and password
     */
    LoginResponseDTO login(LoginDTO loginDTO);

    /**
     * Get the currently logged-in user
     * @return UserDTO of the current user, or null if not logged in
     */
    UserDTO getCurrentUser();

    /**
     * Set the currently logged-in user after successful login
     */
    void setCurrentUser(UserDTO user);

    /**
     * Get the current user's ID
     * @return user ID or null
     */
    Integer getCurrentUserId();
}
