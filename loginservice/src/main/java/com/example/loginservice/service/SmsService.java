package com.example.loginservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsService {

    // 模擬 OTP 儲存：實際應用會使用 Redis 等緩存
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpirationTime = new ConcurrentHashMap<>(); // OTP 過期時間

    private static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000; // OTP 有效期 5 分鐘

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", new Random().nextInt(1000000)); // 生成 6 位數 OTP
        otpStore.put(phoneNumber, otp);
        otpExpirationTime.put(phoneNumber, System.currentTimeMillis() + OTP_VALID_DURATION_MS);
        System.out.println("Generated OTP for " + phoneNumber + ": " + otp);
        // 這裡會呼叫第三方簡訊 API 發送簡訊
        // 例如：twilioClient.messages.create(...);
        return otp;
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        if (!otpStore.containsKey(phoneNumber)) {
            return false; // 電話號碼未生成 OTP
        }
        if (System.currentTimeMillis() > otpExpirationTime.get(phoneNumber)) {
            otpStore.remove(phoneNumber); // 移除過期 OTP
            otpExpirationTime.remove(phoneNumber);
            System.out.println("OTP for " + phoneNumber + " expired.");
            return false; // OTP 已過期
        }
        boolean isValid = otpStore.get(phoneNumber).equals(otp);
        if (isValid) {
            // OTP 成功驗證後移除，防止重放攻擊
            otpStore.remove(phoneNumber);
            otpExpirationTime.remove(phoneNumber);
        }
        return isValid;
    }
}