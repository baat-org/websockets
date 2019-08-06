package org.baat.websockets.config;

import org.baat.websockets.handler.UserSessionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WSConfig implements WebSocketConfigurer {

	@Value("${user_service_uri}")
	private String userServiceURI;

	@Bean
	public UserSessionHandler userSessionHandler() {
		return new UserSessionHandler(userServiceURI);
	}

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
		registry.addHandler(userSessionHandler(), "/baat-ws").setAllowedOrigins("*");
	}

}