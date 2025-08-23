package com.example.connector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
        // 將整個 execute() 方法當成一個單一的 Mono<Void>
        // 將整個執行和驗證的流程封裝成一個 Mono<Void>
        Mono<Void> executeAndVerify = webSocketClient.execute(uri, session -> {
            // 建立一個 Flux 來接收伺服器的回傳
            Mono<String> receivedMessages = session.receive()
                    .map(message -> message.getPayloadAsText())
                    .next(); // 我們只關心第一個訊息

            // 使用 then() 來確保 send() 和驗證的順序
            return session.send(Mono.just(session.textMessage("hello")))
                    .then(receivedMessages)
                    .doOnNext(message -> {
                        System.out.println("Received message: " + message);
                        // 在這裡進行斷言，但不要在 reactive chain 中拋出異常
                        if (!message.equals("world")) {
                            throw new AssertionError("Expected 'world' but got '" + message + "'");
                        }
                    })
                    .then(); // 確保回傳 Mono<Void>
        });

        // 最後用 block() 來阻塞並等待整個非同步流程完成
        executeAndVerify.block(Duration.ofSeconds(5));
    }
}
