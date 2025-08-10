package com.example.loginservice.controller;

import com.example.common.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HealthController {

//    @Value("${spring.security.oauth2.client.registration.google.a}")
//    private List<String> googleClientId;
    @GetMapping("alive")
    public ResponseEntity<ApiResponse> alive() {
        //System.out.println(googleClientId);
        return ResponseEntity.ok().body(ApiResponse.success(LocalDateTime.now().toString()));
    }
}
