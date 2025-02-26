package com.ticketing.RealimeTicketingSystem_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket configuration for enabling WebSocket message handling
 * and message broker for the application.
 * Sets WebSocket communication path and message broker path.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConnect implements WebSocketMessageBrokerConfigurer {

    /**
     * Default constructor for WebSocketConnect.
     * Initializes the WebSocket configuration.
     */
    public WebSocketConnect() {
        // Default constructor
    }

    /**
     * Configure message broker for the application.
     * Enables simple broker with "/topic" prefix.
     * Sets application destination prefix to "/app".
     * @param config (MessageBrokerRegistry for configuring message broker)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register STOMP endpoints for the application.
     * Enables a SockJS endpoint with "/websocket" path.
     * Enables a raw WebSocket endpoint with "/websocket" path.
     * Allows connections from Angular development server.
     * @param registry (StompEndpointRegistry)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();

        registry.addEndpoint("/websocket")
                .setAllowedOrigins("http://localhost:4200");
    }
}
