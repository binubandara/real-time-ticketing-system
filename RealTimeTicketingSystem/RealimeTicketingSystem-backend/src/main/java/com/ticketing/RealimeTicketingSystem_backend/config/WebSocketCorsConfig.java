package com.ticketing.RealimeTicketingSystem_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cross-origin resource sharing (CORS).
 * Allows cross-origin requests from the Angular frontend.
 */
@Configuration
public class WebSocketCorsConfig implements WebMvcConfigurer {

    /**
     * Default constructor for WebSocketCorsConfig.
     * Initializes the CORS configuration.
     */
    public WebSocketCorsConfig() {
        // Default constructor
    }

    /**
     * Add CORS mappings for the application.
     * Allows:
     * - All endpoints to be accessed from http://localhost:4200
     * - HTTP methods: GET, POST, PUT, DELETE, OPTIONS
     * - All headers
     * - Credentials
     * @param registry (CorsRegistry for configuring CORS)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
