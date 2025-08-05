package com.example.loginservice.service;

import com.example.loginservice.common.ApiCommonException;
import com.example.loginservice.common.ResponseCode;
import com.example.loginservice.dto.SignInRequest;
import com.example.loginservice.util.PrefixUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long CACHE_TIME_MILLIS = 5 * 1000 * 60;

    public void sendEmailAndCache(SignInRequest signInRequest, String subject, String body) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.EMAIL, signInRequest.getEmail());

        if(redisTemplate.hasKey(key)) {
            throw new ApiCommonException(ResponseCode.DATA_ALREADY_EXISTS);
        }

        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(signInRequest), Duration.ofMillis(CACHE_TIME_MILLIS)); // 5 分鐘

        sendEmail(signInRequest.getEmail(), subject, body);
    }

    public void sendEmail(String to, String subject, String body) {
        // 實現發送電子郵件的邏輯
        // 例如使用 JavaMailSender 發送郵件
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }

    public void cacheCode(String email, String activationCode) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.EMAIL, email);

        if(redisTemplate.hasKey(key)) {
            throw new ApiCommonException(ResponseCode.DATA_ALREADY_EXISTS);
        }

        redisTemplate.opsForValue().set(key, activationCode,Duration.ofMillis(CACHE_TIME_MILLIS) );
    }

    public SignInRequest getCacheData(String email, String activationCode) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.EMAIL, email);

        String data = redisTemplate.opsForValue().get(key);
        if(data == null) {
            throw new ApiCommonException(ResponseCode.DATA_NOT_FOUND);
        }

        return objectMapper.readValue(data, SignInRequest.class);
    }

    public void validateActivationCode(String email, String activationCode,String  requestCode) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.EMAIL, email);

        if (!activationCode.equals(requestCode)) {
            throw new ApiCommonException(ResponseCode.INVALID_ACTIVATION_CODE);
        }

        // 如果驗證成功，刪除該激活碼
        redisTemplate.delete(key);
    }


}
