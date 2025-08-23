package com.example.connector.handler;

import com.example.proto.common.HeartbeatRequest;
import com.example.proto.common.HeartbeatResponse;
import com.example.proto.common.RequestMessage;
import com.example.proto.common.ResponseMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class HeartBeatMessageHandler implements ProtoMessageHandler{

    @Override
    public boolean test(RequestMessage message) {
        return message.getPayloadCase() == RequestMessage.PayloadCase.HEARTBEAT_REQUEST;
    }
    @Override
    public Mono<ResponseMessage> handle(WebSocketSession session, RequestMessage message) {
        HeartbeatRequest request = message.getHeartbeatRequest();
        HeartbeatResponse response = HeartbeatResponse.newBuilder()
                .setClientTimestampMs(request.getClientTimestampMs())
                .setServerTimestampMs(Instant.now().toEpochMilli())
                .build();

        return Mono.just(ResponseMessage.newBuilder()
                        .setStatus(ResponseMessage.Status.SUCCESS)
                        .setHeartbeatRequest(response)
                .build());
    }

}
