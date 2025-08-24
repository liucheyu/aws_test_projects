package com.example.connector.handler;


import com.example.proto.common.HeartbeatRequest;
import com.example.proto.common.HeartbeatResponse;
import com.example.proto.common.RequestMessage;
import com.example.proto.common.ResponseMessage;
import com.example.proto.game.GameServiceGrpc;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameGrpcHandler extends GameServiceGrpc.GameServiceImplBase {

    @Autowired
    List<ProtoMessageHandler> messageHandlers;

    @Override
    public StreamObserver<RequestMessage> gameStream(StreamObserver<ResponseMessage> responseObserver) {
        Context grpcContext = Context.current();
        return new StreamObserver<RequestMessage>() {
            @Override
            public void onNext(RequestMessage message) {
                ResponseMessage responseMessage = messageHandlers.stream()
                        .filter(hadler -> hadler.test(message))
                        .findFirst()
                        .map(handler -> handler.handle(null, grpcContext, message))
                        .orElse(ResponseMessage.newBuilder().build());

                responseObserver.onNext(responseMessage);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
