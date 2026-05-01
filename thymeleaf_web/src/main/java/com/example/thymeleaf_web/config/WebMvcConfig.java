package com.example.thymeleaf_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration.
 * Configures view controllers and other web-related settings.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map "/" to home view
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/home").setViewName("index");
        
        // Map login page
        registry.addViewController("/login").setViewName("login");
        
        // Map error pages
        registry.addViewController("/403").setViewName("errors/403");
        registry.addViewController("/404").setViewName("errors/404");
        registry.addViewController("/500").setViewName("errors/500");
    }
}