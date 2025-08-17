package com.example.loginservice.service;

import com.example.common.common.ApiCommonException;
import com.example.common.common.ResponseCode;
import com.example.common.config.CacheEnum;
import com.example.loginservice.dto.SignInMobilePhoneRequest;
import com.example.common.util.OptUtil;
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
public class SmsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000; // OTP 有效期 5 分鐘

    public void sendAndCacheOtp(SignInMobilePhoneRequest signInRequest) throws JsonProcessingException {
        String opt = OptUtil.generateOpt(6);
        signInRequest.setVerificationCode(opt);
        cacheObject(opt, signInRequest);
        // 手動調用的話:
//        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.MOBILE_PHONE, opt);
//
//        if (redisTemplate.hasKey(key)) {
//            throw new ApiCommonException(ResponseCode.ACTIVATION_CODE_NOT_EXPIRED);
//        }
//        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(signInRequest), Duration.ofMillis(OTP_VALID_DURATION_MS));

        // 這裡會呼叫第三方簡訊 API 發送簡訊
        // 例如：twilioClient.messages.create(...);
    }

    public SignInMobilePhoneRequest validateOtpAndPop(String phoneNumber, String otp) throws JsonProcessingException {
        SignInMobilePhoneRequest cache = getCacheObject(otp);
        if (phoneNumber.equals(cache.getMobilePhoneNumber())) {
            evictCache(otp); // 驗證成功後刪除 OTP
            return cache;
        }

        // 手動調用的話:
//        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.MOBILE_PHONE, otp);
//        String data = redisTemplate.opsForValue().get(key);
//        SignInMobilePhoneRequest signInMobilePhoneRequest = objectMapper.readValue(data, SignInMobilePhoneRequest.class);
//
//        if (phoneNumber.equals(signInMobilePhoneRequest.getMobilePhoneNumber())) {
//            redisTemplate.delete(key); // 驗證成功後刪除 OTP
//            return signInMobilePhoneRequest;
//        }

        throw new ApiCommonException(
                ResponseCode.INVALID_ACTIVATION_CODE);

    }

    @CachePut(cacheNames = CacheEnum.Prefix.MOBILE_PREFIX, key = "#activationCode")
    public SignInMobilePhoneRequest cacheObject(String activationCode, SignInMobilePhoneRequest signInRequest) {
        //System.out.println("Storing user registration info with activation code: " + activationCode);
        return signInRequest;
    }
    @Cacheable(cacheNames = CacheEnum.Prefix.MOBILE_PREFIX, key = "#activationCode")
    public SignInMobilePhoneRequest getCacheObject(String activationCode) {
        return null; // 快取過期或未命中時返回 null
    }

    @CacheEvict(cacheNames = CacheEnum.Prefix.MOBILE_PREFIX, key = "#activationCode")
    public void evictCache(String activationCode) {
    }

    public void sendAndCacheOtp(String phoneNumber) throws JsonProcessingException {
        String opt = OptUtil.generateOpt(6);
        cacheSingle(phoneNumber, opt);

        // 手動調用
//        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.MOBILE, opt);
//        if (redisTemplate.hasKey(key)) {
//            throw new ApiCommonException(ResponseCode.ACTIVATION_CODE_NOT_EXPIRED);
//        }
//
//
//        redisTemplate.opsForValue().set(key, phoneNumber, Duration.ofMillis(OTP_VALID_DURATION_MS));

        // 這裡會呼叫第三方簡訊 API 發送簡訊
        // 例如：twilioClient.messages.create(...);
    }
    public boolean validateOtp(String phoneNumber, String otp) {
        String cache = getCacheSingle(otp);
        //手動調用:
//        String key = PrefixUtil.getKeyWithPrefix(CacheEnum.MOBILE, otp);
//        String cache = redisTemplate.opsForValue().get(key);

        return phoneNumber.equals(cache);
    }

    @CachePut(cacheNames = CacheEnum.Prefix.MOBILE_PREFIX, key = "#activationCode")
    public String cacheSingle(String activationCode, String phoneNumber) {
        //System.out.println("Storing user registration info with activation code: " + activationCode);
        return phoneNumber;
    }

    @Cacheable(cacheNames = CacheEnum.Prefix.MOBILE_PREFIX, key = "#activationCode")
    public String getCacheSingle(String activationCode) {
        return null; // 快取過期或未命中時返回 null
    }



}