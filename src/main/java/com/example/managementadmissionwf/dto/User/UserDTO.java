package com.example.managementadmissionwf.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User profile data transfer object
 * Contains only user profile information, no authentication data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Integer id;
    private String fullname;
    private String email;
    private String username;
    private RoleDTO role;
}
