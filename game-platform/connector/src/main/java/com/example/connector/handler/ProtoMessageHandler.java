package com.example.connector.handler;

import com.example.proto.common.RequestMessage;
import com.example.proto.common.ResponseMessage;
import com.google.protobuf.Message;
import io.grpc.Context;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public interface ProtoMessageHandler extends Predicate<RequestMessage> {

    ResponseMessage handle(WebSocketSession webSocketSession, Context grpcContext, RequestMessage message);
}
