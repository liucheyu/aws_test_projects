package com.example.connector.handler;

import com.example.connector.handler.ProtoMessageHandler;
import com.example.proto.common.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class GameWebSocketHandler implements WebSocketHandler {

    @Autowired
    List<ProtoMessageHandler> messageHandlers;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .flatMap(message -> {
                    if (message.getType() != WebSocketMessage.Type.BINARY) {
                        return Mono.empty();
                    }

                    try {
                        // 1. 將二進位訊息解序列化為通用的 RequestMessage
                        RequestMessage requestMessage = RequestMessage.parseFrom(
                                message.getPayload().asInputStream()
                        );

                        // 2. 檢測可用的handler
                        // 3. 執行並返回結果
                        return messageHandlers.stream()
                                .filter(handler -> handler.test(requestMessage))
                                .findFirst()
                                .map(hadler -> hadler.handle(session, null, requestMessage))
                                .map(responseMessage -> session.send(Mono.just(session.binaryMessage(factory -> factory.wrap(responseMessage.toByteArray())))))
                                .orElse(Mono.empty());


                    } catch (Exception e) {
                        System.err.println("Failed to parse Protobuf message: " + e.getMessage());
                        // 處理錯誤，例如關閉連線或記錄日誌
                        return Mono.empty();
                    }

                }).then();
    }

}
