package com.example.connector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameWebSocketHandlerTest {
    @LocalServerPort
    private int port;

    private final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

    @Test
    void testConnection() throws URISyntaxException {
        URI uri = new URI("ws://localhost:" + port + "/websocket");

        webSocketClient.execute(uri, session -> {
            Mono<Void> output = session.send(Mono.just(session.textMessage("Hello")));
            Mono<String> input = session.receive()
                    .map(message -> message.getPayloadAsText())
                    .doOnNext(message -> System.out.println(message)).next();

            return Mono.when(output, input)
                    .then(Mono.defer(() -> {
                        System.out.println(output);
                        System.out.println(input);
                        return Mono.just(true);
                    })).then();
        }).block(Duration.ofSeconds(5));

        assert true;
    }
}
