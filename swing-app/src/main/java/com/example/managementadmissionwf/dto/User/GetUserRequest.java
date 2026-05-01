package com.example.managementadmissionwf.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRequest {
    private String keyword;
    private String role;
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int limit = 10;
    private Integer currentUserId; // exclude this user from results
}
