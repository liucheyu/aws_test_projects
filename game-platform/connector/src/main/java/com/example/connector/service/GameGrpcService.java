package com.example.connector.service;

import com.example.proto.GameServiceGrpc;
import com.example.proto.HeartbeatRequest;
import com.example.proto.HeartbeatResponse;
import io.grpc.stub.StreamObserver;

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
