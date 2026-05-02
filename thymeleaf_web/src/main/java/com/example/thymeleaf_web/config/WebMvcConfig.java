package com.example.thymeleaf_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/tra-cuu-diem");
        registry.addViewController("/404").setViewName("errors/404");
        registry.addViewController("/500").setViewName("errors/500");
    }
}