package com.example.connector.interceptor;

import com.example.common.util.JwtUtil;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
//import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Order(100)
@GlobalServerInterceptor
@Component
@Slf4j
@RequiredArgsConstructor
public class GrpcLifeInterceptor
        implements ServerInterceptor
{

    private final JwtUtil jwtUtil;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        // --- 1. 攔截請求：在這裡執行「連線建立」時需要的前置邏輯 ---
        // 這裡的邏輯會在每個 RPC 呼叫開始時被執行
        String token = metadata.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));
        log.info("New RPC call started for method: {}, received token: {}", serverCall.getMethodDescriptor().getFullMethodName(), token);

        // 如果 token 無效，你可以直接關閉呼叫
        if (token == null || !isValid(token)) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Token is invalid"), metadata);
            return new ServerCall.Listener<ReqT>() {}; // 返回一個空的 Listener
        }

        // 將處理權移交給下一個攔截器或 gRPC 服務實作
        ServerCall.Listener<ReqT> originalListener = serverCallHandler.startCall(serverCall, metadata);

        // --- 2. 返回一個包裝過的 Listener，以便在呼叫結束後執行後置邏輯 ---
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(originalListener) {
            @Override
            public void onComplete() {
                // 在這裡執行「連線結束」時需要的後置邏輯
                // 這個方法會在 RPC 呼叫成功完成時被呼叫
                log.info("RPC call completed successfully for method: {}", serverCall.getMethodDescriptor().getFullMethodName());
                super.onComplete();
            }

            @Override
            public void onCancel() {
                // 如果 RPC 呼叫被客戶端或伺服器取消，這個方法會被呼叫
                log.warn("RPC call was cancelled for method: {}", serverCall.getMethodDescriptor().getFullMethodName());
                super.onCancel();
            }
        };
    }

    private boolean isValid(String token) {
        String sub = jwtUtil.extractSubject(token);
        return sub != null && !sub.isBlank() && !jwtUtil.isTokenExpired(token);
    }
}
