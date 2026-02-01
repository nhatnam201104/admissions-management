package com.example.managementadmissionwf.dto.Auth;

import com.example.managementadmissionwf.dto.User.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login response data transfer object
 * Contains authentication result and user profile
 * Designed to be extensible for future JWT/session integration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private UserDTO user;
    
    // Fields reserved for future JWT integration
    // private String token;
    // private String refreshToken;
    // private LocalDateTime expiresAt;
    
    /**
     * Create a successful login response
     * @param user The authenticated user
     * @return LoginResponseDTO with success=true
     */
    public static LoginResponseDTO success(UserDTO user) {
        return LoginResponseDTO.builder()
                .success(true)
                .message("Login successful")
                .user(user)
                .build();
    }
}