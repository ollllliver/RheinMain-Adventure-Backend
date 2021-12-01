package de.hsrm.mi.swt.rheinmainadventure.messaging;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Konfiguration des Messagebrokers
 */
@Configuration
@EnableWebSocketMessageBroker
public class LobbyMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix für alle zugehörigen Destinations,
        // z.B. /topic/news, /topic/offers usw.
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
<<<<<<< HEAD
        registry.addEndpoint("/messagebroker").setAllowedOrigins("*");
=======
        //registry.addEndpoint("/messagebroker").setAllowedOrigins("*");
>>>>>>> 59a8e7d260f6f3eb823e9f2aa6572eb5cd10ae3d
        registry.addEndpoint("/messagebroker").withSockJS();
    }   
}
