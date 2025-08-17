package com.example.connector.service;

import com.example.connector.proto.GameServiceGrpc;
import com.example.game.proto.HeartbeatRequest;
import com.example.game.proto.HeartbeatResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
public class GameGrpcService extends GameServiceGrpc.GameServiceImplBase {
    @Override
    public void checkHeartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        //super.checkHeartbeat(request, responseObserver);

        responseObserver.onNext(HeartbeatResponse.
                newBuilder()
                .setServerTimestampMs(System.currentTimeMillis())
                .setClientTimestampMs(request.getClientTimestampMs())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void streamCheckHeartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        //super.streamCheckHeartbeat(request, responseObserver);

        responseObserver.onNext(HeartbeatResponse.
                newBuilder()
                        .setServerTimestampMs(System.currentTimeMillis())
                        .setClientTimestampMs(request.getClientTimestampMs())
                .build());

        //responseObserver.onError(throwble -> {});

    }
}
