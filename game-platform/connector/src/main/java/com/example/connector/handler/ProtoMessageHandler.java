package com.example.connector.handler;

import com.example.proto.common.RequestMessage;
import com.example.proto.common.ResponseMessage;
import com.google.protobuf.Message;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public interface ProtoMessageHandler extends Predicate<RequestMessage> {

    Mono<ResponseMessage> handle(WebSocketSession session, RequestMessage message);
}
