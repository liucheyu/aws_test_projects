package com.example.loginservice.controller;

import com.example.loginservice.common.ApiCommonException;
import com.example.loginservice.common.ApiResponse;
import com.example.loginservice.common.ResponseCode;
import com.example.loginservice.dto.SignInEmailRequest;
import com.example.loginservice.dto.SignInMobilePhoneRequest;
import com.example.loginservice.model.User;
import com.example.loginservice.repository.UserRepository;
import com.example.loginservice.service.EmailService;
import com.example.loginservice.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @GetMapping("/profile")
    public String getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return "Hello, " + username + "! You are authenticated.";
    }

    @PostMapping("/sign-in/email")
    public ResponseEntity<ApiResponse> signInEmail(@RequestBody SignInEmailRequest signInEmailRequest) {
        Optional<User> byUsername = userRepository.findByUsername(signInEmailRequest.getEmail());
        byUsername.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.USER_ALREADY_EXISTS,
                    ResponseCode.USER_ALREADY_EXISTS.getMessage());
        });

        emailService.sendEmail(signInEmailRequest.getEmail(), "Welcome to our service", "Thank you for signing up!");

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @PostMapping("/sign-in/mobile-phone-Number")
    public ResponseEntity<ApiResponse> signIn(@RequestBody SignInMobilePhoneRequest signInRequest) {
        Optional<User> byUsername = userRepository.findByUsername(signInRequest.getMobilePhoneNumber() + "");
        byUsername.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.PHONE_NUMBER_ALREADY_USED,
                    ResponseCode.PHONE_NUMBER_ALREADY_USED.getMessage());
        });

        String phoneNumber = signInRequest.getMobilePhoneNumber() + "";
        if(phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        smsService.generateOtp(signInRequest.getCountryCode() + phoneNumber);

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @GetMapping("/admin-only")
    public String getAdminOnlyData() {
        return "This is admin-only data!";
    }
}