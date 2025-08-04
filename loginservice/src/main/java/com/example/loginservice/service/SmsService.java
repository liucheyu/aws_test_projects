package com.example.loginservice.service;

import com.example.loginservice.util.PrefixUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final RedisTemplate<String,String> redisTemplate;
    private static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000; // OTP 有效期 5 分鐘

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", new Random().nextInt(1000000)); // 生成 6 位數 OTP
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.MOBILE_PHONE, phoneNumber);
        redisTemplate.opsForValue().set(key, otp, Duration.ofMillis(OTP_VALID_DURATION_MS));
        // 這裡會呼叫第三方簡訊 API 發送簡訊
        // 例如：twilioClient.messages.create(...);
        return otp;
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.MOBILE_PHONE, phoneNumber);
        String cacheOtp = redisTemplate.opsForValue().get(key);

        return otp.equals(cacheOtp);
    }

}