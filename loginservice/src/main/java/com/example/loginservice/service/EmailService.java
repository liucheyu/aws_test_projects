package com.example.loginservice.service;

import com.example.loginservice.common.ApiCommonException;
import com.example.loginservice.common.ResponseCode;
import com.example.loginservice.util.PrefixUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final RedisTemplate<String,String> redisTemplate;

    public void sendEmail(String to, String subject, String body) {
        String key = PrefixUtil.getKeyWithPrefix(PrefixUtil.Prefix.EMAIL, to);
        String cacheEmail = redisTemplate.opsForValue().get(key);
        if(cacheEmail != null) {
            throw new ApiCommonException(ResponseCode.DATA_ALREADY_EXISTS);
        }

        // 實現發送電子郵件的邏輯
        // 例如使用 JavaMailSender 發送郵件
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }

}
