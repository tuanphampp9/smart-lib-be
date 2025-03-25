package com.tuanpham.smart_lib_be.config;
import com.tuanpham.smart_lib_be.util.SecurityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    private final SecurityUtil securityUtil;

    public WebsocketConfig(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");// Client sẽ subscribe vào "/topic/notifications"
        registry.setApplicationDestinationPrefixes("/app");// Route cho API gửi tin
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketAuthInterceptor(securityUtil));
    }

}
