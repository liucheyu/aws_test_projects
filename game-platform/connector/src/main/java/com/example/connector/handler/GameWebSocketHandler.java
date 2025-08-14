package com.example.connector.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class GameWebSocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {

        return session.receive().map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .doOnNext(message -> {
                    System.out.println(message);
                }).then();
    }


}
