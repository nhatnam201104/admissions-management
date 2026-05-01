package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.User.*;
import com.example.managementadmissionwf.dto.common.ApiResponse;
import com.example.managementadmissionwf.dto.common.Paging;

public interface UserService {
    ApiResponse<Paging<GetUserResponse>> getUsers(GetUserRequest request);
    ApiResponse<CreateUserResponse> createUser(CreateUserRequest request);
    ApiResponse<UpdateUserResponse> updateUser(UpdateUserRequest request);
    ApiResponse<Void> deleteUser(Integer id);
}
