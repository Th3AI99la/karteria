package com.projeto.karteria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Onde o cliente vai 'escutar' (assinar) as mensagens
        config.enableSimpleBroker("/topic");
        // Prefixo para mensagens que vão do cliente para o servidor (não usaremos muito agora)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // O "tubo" principal. O SockJS permite funcionar até em navegadores velhos.
        registry.addEndpoint("/ws").withSockJS();
    }
}