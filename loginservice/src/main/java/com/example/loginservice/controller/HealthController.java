package com.example.loginservice.controller;

import com.example.loginservice.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HealthController {

    @GetMapping("alive")
    public ResponseEntity<ApiResponse> alive() {
        return ResponseEntity.ok().body(ApiResponse.success(LocalDateTime.now().toString()));
    }
}
