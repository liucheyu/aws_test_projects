package com.example.loginservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")  // 雖然沒有具體方法，但保留這個 Controller 結構，或者您可以完全刪除這個文件
//@RequiredArgsConstructor
public class AuthController {
    // 現在所有登入邏輯都由 UnifiedLoginFilter 處理
    // 登出邏輯由 Spring Security 內建的 logout Filter 處理

//    private final AuthenticationManager authenticationManager;
//    private final UserDetailsService userDetailsService;
//    private final JwtUtil jwtUtil;
//    private final SmsService smsService; // 假設有一個 SmsService 用於處理 SMS 發送和 OTP 驗證
//
//    // 帳號密碼登入
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
//        );
//
//        // 如果認證成功，生成 JWT
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String jwt = jwtUtil.generateToken(userDetails);
//
//        return ResponseEntity.ok("{\"token\":\"" + jwt + "\"}");
//    }
//
//    // 登出操作由 Spring Security 的 /api/auth/logout 自動處理
//    // 前端需要清除本地的 JWT token
//
//    @PostMapping("/send-otp")
//    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest request) {
//        // 在這裡可以加入一些限制，防止頻繁發送，例如基於IP或電話號碼的限流
//        smsService.generateOtp(request.getPhoneNumber());
//        return ResponseEntity.ok("{\"message\":\"OTP sent successfully to " + request.getPhoneNumber() + "\"}");
//    }
//
//    // 新增：電話號碼 + OTP 登入
//    @PostMapping("/login-by-sms")
//    public ResponseEntity<String> loginBySms(@RequestBody SmsLoginRequest request) {
//        // 創建自定義的 AuthenticationToken
//        SmsAuthenticationToken authToken = new SmsAuthenticationToken(request.getPhoneNumber(), request.getOtp());
//
//        // 交給 AuthenticationManager 處理，它會找到 SmsAuthenticationProvider
//        Authentication authentication = authenticationManager.authenticate(authToken);
//
//        // 如果認證成功，生成 JWT
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String jwt = jwtUtil.generateToken(userDetails);
//
//        return ResponseEntity.ok("{\"token\":\"" + jwt + "\"}");
//    }
}