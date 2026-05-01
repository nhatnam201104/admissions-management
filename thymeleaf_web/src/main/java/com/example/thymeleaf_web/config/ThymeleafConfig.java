package com.example.thymeleaf_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Thymeleaf configuration.
 * Customizes Thymeleaf template engine behavior.
 */
@Configuration
public class ThymeleafConfig {
    
    /**
     * Configure template resolver.
     * Defines how and where Thymeleaf resolves templates.
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false); // Disable cache for development
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }
    
    /**
     * Configure template engine.
     * Combines template resolver with dialects.
     * Note: Spring Boot auto-configures SpringSecurityDialect when thymeleaf-extras-springsecurity6 is in classpath
     */
    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        // Spring Security dialect is auto-configured by Spring Boot
        return templateEngine;
    }
}