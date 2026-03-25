package com.example.managementadmissionwf.config;

import com.example.managementadmissionwf.ui.component.Navigation;
import com.example.managementadmissionwf.ui.frame.MainFrame;
import com.example.managementadmissionwf.ui.panel.UserManagementPanel;
import com.example.managementadmissionwf.ui.panel.BonusScorePanel;
import com.example.managementadmissionwf.ui.panel.ConversionTablePanel;
import com.example.managementadmissionwf.ui.panel.subjectgroup.SubjectGroupPanel;
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

    /**
     * Register UserManagementPanel as a singleton bean
     */
    @Bean
    public UserManagementPanel userManagementPanel() {
        return new UserManagementPanel();
    }

    /**
     * Register BonusScorePanel as a singleton bean
     */
    @Bean
    public BonusScorePanel bonusScorePanel() {
        return new BonusScorePanel();
    }

    /**
     * Register ConversionTablePanel as a singleton bean
     */
    @Bean
    public ConversionTablePanel conversionTablePanel() {
        return new ConversionTablePanel();
    }

    /**
     * Register SubjectGroupPanel as a singleton bean
     */
    @Bean
    public SubjectGroupPanel subjectGroupPanel() {
        return new SubjectGroupPanel();
    }

}