package com.example.thymeleaf_web.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super("RESOURCE_NOT_FOUND", String.format("%s with id %d not found", resourceName, id));
    }
    
    public ResourceNotFoundException(String resourceName, String identifier) {
        super("RESOURCE_NOT_FOUND", String.format("%s with identifier '%s' not found", resourceName, identifier));
    }
}