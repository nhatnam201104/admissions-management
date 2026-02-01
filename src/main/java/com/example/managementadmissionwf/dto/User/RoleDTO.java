package com.example.managementadmissionwf.dto.User;

/**
 * Role enum for DTO layer - decoupled from entity layer
 */
public enum RoleDTO {
    STUDENT,
    ADMIN,
    MANAGER;
    
    /**
     * Convert from entity enum to DTO enum
     * @param role Entity role enum
     * @return DTO role enum
     */
    public static RoleDTO fromEntityRole(com.example.managementadmissionwf.dal.entity.RoleUser role) {
        if (role == null) {
            return null;
        }
        return RoleDTO.valueOf(role.name());
    }
    
    /**
     * Convert DTO enum to entity enum
     * @return Entity role enum
     */
    public com.example.managementadmissionwf.dal.entity.RoleUser toEntityRole() {
        return com.example.managementadmissionwf.dal.entity.RoleUser.valueOf(this.name());
    }
}