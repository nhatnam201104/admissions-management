package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dto.User.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * User entity to DTO mapper
 */
@Mapper(componentModel = "Spring")
public interface UserMapper {
    
    @Mapping(target = "role", expression = "java(mapRole(user.getRole()))")
    UserDTO toUserDTO(Users user);

    @Mapping(target = "role", expression = "java(mapRole(user.getRole()))")
    CreateUserResponse toCreateResponse(Users user);

    @Mapping(target = "role", expression = "java(mapRole(user.getRole()))")
    UpdateUserResponse toUpdateResponse(Users user);

    @Mapping(target = "role", expression = "java(mapRole(user.getRole()))")
    GetUserResponse toGetUserResponse(Users user);

    List<GetUserResponse> toGetUserResponses(List<Users> users);
    
    /**
     * Map entity role enum to DTO role enum
     */
    default RoleDTO mapRole(com.example.managementadmissionwf.dal.entity.RoleUser role) {
        if (role == null) {
            return null;
        }
        return RoleDTO.valueOf(role.name());
    }
}
