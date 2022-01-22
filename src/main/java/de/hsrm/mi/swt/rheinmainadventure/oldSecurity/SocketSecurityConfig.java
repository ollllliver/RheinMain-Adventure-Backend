package de.hsrm.mi.swt.rheinmainadventure.oldSecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SocketSecurityConfig
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        //
        //messages.anyMessage().authenticated();
        messages.simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT, SimpMessageType.UNSUBSCRIBE,SimpMessageType.DISCONNECT).permitAll()
                .anyMessage().permitAll()
                .simpDestMatchers("/**").permitAll()
                .simpDestMatchers("/messagebroker/**","/gamebroker/**").permitAll();
                //.anyMessage().authenticated();
    }

    // Disable CSRF for testing
    @Override
    protected boolean sameOriginDisabled() {
        return false;
    }
}