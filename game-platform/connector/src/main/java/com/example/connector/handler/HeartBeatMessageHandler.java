package com.example.connector.handler;

import com.example.connector.handler.ProtoMessageHandler;
import com.example.proto.common.HeartbeatRequest;
import com.example.proto.common.HeartbeatResponse;
import com.example.proto.common.RequestMessage;
import com.example.proto.common.ResponseMessage;
import io.grpc.Context;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class HeartBeatMessageHandler implements ProtoMessageHandler {

    @Override
    public boolean test(RequestMessage message) {
        return message.getPayloadCase() == RequestMessage.PayloadCase.HEARTBEAT_REQUEST;
    }
    @Override
    public ResponseMessage handle(WebSocketSession session, Context grpcContext, RequestMessage message) {
        HeartbeatRequest request = message.getHeartbeatRequest();
        HeartbeatResponse response = HeartbeatResponse.newBuilder()
                .setClientTimestampMs(request.getClientTimestampMs())
                .setServerTimestampMs(Instant.now().toEpochMilli())
                .build();

        return ResponseMessage.newBuilder()
                        .setStatus(ResponseMessage.Status.SUCCESS)
                        .setHeartbeatRequest(response)
                .build();
    }

}
