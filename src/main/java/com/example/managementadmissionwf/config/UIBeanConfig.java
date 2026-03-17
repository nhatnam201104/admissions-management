package com.example.managementadmissionwf.config;

import com.example.managementadmissionwf.ui.frame.MainFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration class for UI beans
 * Registers UI components as Spring beans
 */
@Configuration
public class UIBeanConfig {

    /**
     * Register MainFrame as a prototype bean
     * Prototype scope allows multiple instances
     */
    @Bean
    @Scope("prototype")
    public MainFrame mainFrame() {
        return new MainFrame();
    }
}