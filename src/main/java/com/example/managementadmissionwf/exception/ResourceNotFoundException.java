package com.example.managementadmissionwf.exception;

/**
 * Thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {
    private final String errorCode;
    
    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }
    
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s not found with id: %s", resourceName, id));
        this.errorCode = "RESOURCE_NOT_FOUND";
    }
    
    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(String.format("%s not found with %s: %s", resourceName, field, value));
        this.errorCode = "RESOURCE_NOT_FOUND";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
