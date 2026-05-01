package com.example.thymeleaf_web.model.dto.response;

import com.example.thymeleaf_web.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response.
 * Used to return user data to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
    
    /**
     * Convert User entity to UserResponse DTO.
     */
    public static UserResponse fromEntity(com.example.thymeleaf_web.model.entity.User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(formatDateTime(user.getCreatedAt()))
                .updatedAt(formatDateTime(user.getUpdatedAt()))
                .build();
    }
    
    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? DateUtils.formatDisplayDateTime(dateTime) : "";
    }
}