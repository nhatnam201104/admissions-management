package com.example.thymeleaf_web.controller.advice;

import com.example.thymeleaf_web.exception.BusinessException;
import com.example.thymeleaf_web.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for all controllers.
 * Handles exceptions and returns appropriate error pages.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle resource not found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        log.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("errorCode", ex.getErrorCode());
        return "errors/404";
    }
    
    /**
     * Handle business exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleBusinessException(BusinessException ex, Model model) {
        log.error("Business error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("errorCode", ex.getErrorCode());
        return "errors/500";
    }
    
    /**
     * Handle access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.error("Access denied: {}", ex.getMessage());
        model.addAttribute("error", "You don't have permission to access this resource");
        model.addAttribute("errorCode", "ACCESS_DENIED");
        return "errors/403";
    }
    
    /**
     * Handle page not found (404).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        log.error("No handler found: {}", ex.getRequestURL());
        model.addAttribute("error", "The page you're looking for doesn't exist");
        model.addAttribute("errorCode", "PAGE_NOT_FOUND");
        return "errors/404";
    }
    
    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);
        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorCode", "INTERNAL_ERROR");
        // Don't expose stack trace to users
        return "errors/500";
    }
}