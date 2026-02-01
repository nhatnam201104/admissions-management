package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dto.User.UserDTO;
import com.example.managementadmissionwf.dto.User.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User entity to DTO mapper
 */
@Mapper(componentModel = "Spring")
public interface UserMapper {
    
    @Mapping(target = "role", expression = "java(mapRole(user.getRole()))")
    UserDTO toUserDTO(Users user);
    
    /**
     * Map entity role enum to DTO role enum
     * @param role Entity role
     * @return DTO role
     */
    default RoleDTO mapRole(com.example.managementadmissionwf.dal.entity.RoleUser role) {
        if (role == null) {
            return null;
        }
        return RoleDTO.valueOf(role.name());
    }
}
