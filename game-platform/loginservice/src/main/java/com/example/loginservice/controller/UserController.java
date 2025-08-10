package com.example.loginservice.controller;

import com.example.common.common.ApiCommonException;
import com.example.common.common.ApiResponse;
import com.example.common.common.ResponseCode;
import com.example.loginservice.dto.SignInRequest;
import com.example.loginservice.dto.SignInMobilePhoneRequest;
import com.example.loginservice.model.User;
import com.example.loginservice.repository.UserRepository;
import com.example.loginservice.service.EmailService;
import com.example.loginservice.service.SmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/profile")
    public String getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return "Hello, " + username + "! You are authenticated.";
    }

    @PostMapping("/sign-in/email")
    public ResponseEntity<ApiResponse> signInEmail(@RequestBody SignInRequest signInRequest) throws JsonProcessingException {
        Optional<User> byUsername = userRepository.findByUsername(signInRequest.getEmail());
        byUsername.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.USER_ALREADY_EXISTS,
                    ResponseCode.USER_ALREADY_EXISTS.getMessage());
        });

        // 傳送啟動碼
        emailService.sendEmailAndCache(signInRequest, "Welcome to our service", "Thank you for signing up!");

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @GetMapping("/sign-in/email-activate")
    public ResponseEntity<ApiResponse> signInEmailActivate(@RequestParam("email") String email,
                                                           @RequestParam("activationCode") String activationCode) throws JsonProcessingException {

        SignInRequest cacheData = emailService.getCacheData(email, activationCode);
        emailService.validateActivationCode(email, activationCode, cacheData.getEmail());
        User user = new User();
        user.setUsername(cacheData.getEmail());
        user.setEmail(cacheData.getEmail());
        user.setDisplayName(cacheData.getEmail().split("@")[0]);
        user.setPassword(passwordEncoder.encode(cacheData.getPassword()));
        user.setIsActive(true);
        user.setRoles(List.of("ROLE_USER"));
        userRepository.save(user);

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @PostMapping("/sign-in/mobile-phone-Number")
    public ResponseEntity<ApiResponse> signInMobile(@RequestBody SignInMobilePhoneRequest signInRequest) throws JsonProcessingException {
        Optional<User> byUsername = userRepository.findByUsername(signInRequest.getEmail());
        byUsername.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.PHONE_NUMBER_ALREADY_USED,
                    ResponseCode.PHONE_NUMBER_ALREADY_USED.getMessage());
        });
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(signInRequest.getMobilePhoneNumber() + "");
        byPhoneNumber.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.PHONE_NUMBER_ALREADY_USED,
                    ResponseCode.PHONE_NUMBER_ALREADY_USED.getMessage());
        });

        String phoneNumber = signInRequest.getMobilePhoneNumber() + "";
        if(phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        smsService.sendAndCacheOtp(signInRequest);

        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @PostMapping("/sign-in/mobile-phone-Number-validate")
    public ResponseEntity<ApiResponse> signInMobileValidate(@RequestBody SignInMobilePhoneRequest signInRequest) throws JsonProcessingException {
        Optional<User> byUsername = userRepository.findByUsername(signInRequest.getEmail());
        byUsername.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.PHONE_NUMBER_ALREADY_USED,
                    ResponseCode.PHONE_NUMBER_ALREADY_USED.getMessage());
        });
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(signInRequest.getMobilePhoneNumber() + "");
        byPhoneNumber.ifPresent(u -> {
            throw new ApiCommonException(
                    ResponseCode.PHONE_NUMBER_ALREADY_USED,
                    ResponseCode.PHONE_NUMBER_ALREADY_USED.getMessage());
        });

        String phoneNumber = signInRequest.getMobilePhoneNumber() + "";
        if(phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        if(!StringUtils.hasLength(signInRequest.getVerificationCode())) {
            throw new ApiCommonException(
                    ResponseCode.INVALID_ACTIVATION_CODE);
        }

        SignInMobilePhoneRequest cacheData = smsService.validateOtpAndPop(phoneNumber, signInRequest.getVerificationCode());
        User user = new User();
        user.setUsername(cacheData.getEmail());
        user.setEmail(cacheData.getEmail());
        user.setDisplayName(cacheData.getEmail().split("@")[0]);
        user.setPassword(passwordEncoder.encode(cacheData.getPassword()));
        if(cacheData.getMobilePhoneNumber().startsWith("0")) {
            cacheData.setMobilePhoneNumber(cacheData.getMobilePhoneNumber().substring(1));
        }
        user.setPhoneNumber(cacheData.getCountryCode() + "-" + cacheData.getMobilePhoneNumber());
        user.setIsActive(true);
        user.setRoles(List.of("ROLE_USER"));
        userRepository.save(user);


        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @GetMapping("/admin-only")
    public String getAdminOnlyData() {
        return "This is admin-only data!";
    }

    public void cacheUserLoginData(String key, Object cacheData) {
        redisTemplate.opsForValue().set(key, objectMapper.valueToTree(cacheData).toString());
    }

}