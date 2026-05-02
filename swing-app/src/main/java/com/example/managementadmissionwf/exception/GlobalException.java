package com.example.managementadmissionwf.exception;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * Global exception handler for uncaught Swing UI exceptions
 * Provides user-friendly error messages in desktop application
 */
@Slf4j
public class GlobalException implements Thread.UncaughtExceptionHandler {
    
    private final Frame parentFrame;
    
    public GlobalException() {
        this(null);
    }
    
    public GlobalException(Frame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
//        log.error("Uncaught exception in thread: {}", t.getName(), e);
        
        SwingUtilities.invokeLater(() -> {
            String title = "Application Error";
            String message = buildMessage(e);
            int messageType = JOptionPane.ERROR_MESSAGE;
            
            // Show error dialog
            if (parentFrame != null) {
                JOptionPane.showMessageDialog(parentFrame, message, title, messageType);
            } else {
                JOptionPane.showMessageDialog(null, message, title, messageType);
            }
        });
    }

    /**
     * Build user-friendly error message from exception
     * Hides technical details from end users
     */
    private String buildMessage(Throwable e) {
        // Domain exceptions show friendly message
        if (e instanceof AuthenticationException) {
            return "Authentication failed: " + e.getMessage();
        }
        if (e instanceof ResourceNotFoundException) {
            return "Resource not found: " + e.getMessage();
        }
        if (e instanceof BusinessException) {
            return "Business error: " + e.getMessage();
        }
        
        // Technical exceptions show generic message
        log.debug("Technical exception details", e);
        return "An unexpected error occurred. Please contact support.";
    }
}
