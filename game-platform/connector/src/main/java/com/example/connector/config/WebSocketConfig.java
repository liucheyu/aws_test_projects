package com.example.connector.config;

import com.example.connector.handler.GameWebSocketHandler;
import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
//import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping handlerMapping(GameWebSocketHandler webSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websocket", webSocketHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

}
