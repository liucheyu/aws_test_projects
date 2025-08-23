package com.example.connector.service;


import com.example.proto.common.HeartbeatRequest;
import com.example.proto.common.HeartbeatResponse;
import com.example.proto.game.GameServiceGrpc;
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
    }
    //    @Override
//    public void checkHeartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
//        //super.checkHeartbeat(request, responseObserver);
//
//        responseObserver.onNext(HeartbeatResponse.
//                newBuilder()
//                .setServerTimestampMs(System.currentTimeMillis())
//                .setClientTimestampMs(request.getClientTimestampMs())
//                .build());
//        responseObserver.onCompleted();
//    }

//    @Override
//    public StreamObserver<HeartbeatRequest> streamCheckHeartbeat(StreamObserver<HeartbeatResponse> responseObserver) {
//
//        return  responseObserver.onNext(HeartbeatResponse.
//                newBuilder()
//                .setServerTimestampMs(System.currentTimeMillis())
//
//                .build());
//    }


//    @Override
//    public void streamCheckHeartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
//        super.streamCheckHeartbeat(request, responseObserver);
//    }
}
