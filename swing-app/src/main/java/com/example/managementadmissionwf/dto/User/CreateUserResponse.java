package com.example.managementadmissionwf.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {
    private Integer id;
    private String fullname;
    private String email;
    private String username;
    private RoleDTO role;
    private LocalDate createdAt;
}
