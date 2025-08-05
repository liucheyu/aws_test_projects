package com.example.loginservice.service;

import com.example.loginservice.common.ApiCommonException;
import com.example.loginservice.common.ResponseCode;
import com.example.loginservice.dto.SignInMobilePhoneRequest;
import com.example.loginservice.util.OptUtil;
import com.example.loginservice.util.PrefixUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000; // OTP 有效期 5 分鐘

    public void sendAndCacheOtp(String phoneNumber) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.MOBILE_PHONE, phoneNumber);
        String opt = OptUtil.generateOpt(6);
        redisTemplate.opsForValue().set(key, opt, Duration.ofMillis(OTP_VALID_DURATION_MS));
        // 這裡會呼叫第三方簡訊 API 發送簡訊
        // 例如：twilioClient.messages.create(...);
    }

    public void sendAndCacheOtp(SignInMobilePhoneRequest signInRequest) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.MOBILE_PHONE, signInRequest.getEmail());
        String opt = OptUtil.generateOpt(6);
        signInRequest.setVerificationCode(opt);
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(signInRequest), Duration.ofMillis(OTP_VALID_DURATION_MS));
        // 這裡會呼叫第三方簡訊 API 發送簡訊
        // 例如：twilioClient.messages.create(...);
    }

    public SignInMobilePhoneRequest validateOtpAndPop(String phoneNumber, String otp) throws JsonProcessingException {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.MOBILE_PHONE, phoneNumber);
        String data = redisTemplate.opsForValue().get(key);
        SignInMobilePhoneRequest signInMobilePhoneRequest = objectMapper.readValue(data, SignInMobilePhoneRequest.class);

        if (signInMobilePhoneRequest.getVerificationCode().equals(otp)) {
            redisTemplate.delete(key); // 驗證成功後刪除 OTP
            return signInMobilePhoneRequest;
        }
        throw new ApiCommonException(
                ResponseCode.INVALID_ACTIVATION_CODE);

    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.MOBILE_PHONE, phoneNumber);
        String cacheOtp = redisTemplate.opsForValue().get(key);

        return otp.equals(cacheOtp);
    }


}