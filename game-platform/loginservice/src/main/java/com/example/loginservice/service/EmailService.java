package com.example.loginservice.service;

import com.example.common.common.ApiCommonException;
import com.example.common.common.ResponseCode;
import com.example.common.config.CacheEnum;
import com.example.common.util.OptUtil;
import com.example.loginservice.dto.SignInRequest;
import com.example.common.util.PrefixUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
        String opt = OptUtil.generateOpt(6);
        cacheObject(opt, signInRequest);

        //手動調用
//        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.EMAIL, signInRequest.getEmail());
//
//        if(redisTemplate.hasKey(key)) {
//            throw new ApiCommonException(ResponseCode.DATA_ALREADY_EXISTS);
//        }
//
//        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(signInRequest), Duration.ofMillis(CACHE_TIME_MILLIS)); // 5 分鐘

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
        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.EMAIL, email);

        if(redisTemplate.hasKey(key)) {
            throw new ApiCommonException(ResponseCode.DATA_ALREADY_EXISTS);
        }

        redisTemplate.opsForValue().set(key, activationCode,Duration.ofMillis(CACHE_TIME_MILLIS) );
    }

    public SignInRequest popCacheDataAndValidate(String email, String activationCode) throws JsonProcessingException {
        SignInRequest cacheObject = getCacheObject(activationCode);
        if(cacheObject == null) {
            throw new ApiCommonException(ResponseCode.DATA_NOT_FOUND);
        }

        if(!cacheObject.getEmail().equals(email)) {
            throw new ApiCommonException(ResponseCode.EMAIL_NOT_MATCH);
        }

        return cacheObject;
//        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.EMAIL, email);
//
//        String data = redisTemplate.opsForValue().get(key);
//        if(data == null) {
//            throw new ApiCommonException(ResponseCode.DATA_NOT_FOUND);
//        }
//
//        return objectMapper.readValue(data, SignInRequest.class);
    }

    @CachePut(cacheNames = CacheEnum.Prefix.EMAIL_PREFIX, key = "#activationCode")
    public SignInRequest cacheObject(String activationCode, SignInRequest signInRequest) {
        //System.out.println("Storing user registration info with activation code: " + activationCode);
        return signInRequest;
    }
    @Cacheable(cacheNames = CacheEnum.Prefix.EMAIL_PREFIX, key = "#activationCode")
    public SignInRequest getCacheObject(String activationCode) {
        return null; // 快取過期或未命中時返回 null
    }

    @CacheEvict(cacheNames = CacheEnum.Prefix.EMAIL_PREFIX, key = "#activationCode")
    public void evictCache(String activationCode) {
    }

    public void validateActivationCode(String email, String activationCode,String  requestCode) throws JsonProcessingException {

        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.EMAIL, email);

        if (!activationCode.equals(requestCode)) {
            throw new ApiCommonException(ResponseCode.INVALID_ACTIVATION_CODE);
        }

        // 如果驗證成功，刪除該激活碼
        redisTemplate.delete(key);
    }

    public boolean validateActivationCode(String email, String activationCode) {
        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.EMAIL, email);

        String cacheOtp = redisTemplate.opsForValue().get(key);

        return activationCode.equals(cacheOtp);
    }


}
