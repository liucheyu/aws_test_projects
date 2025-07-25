package com.example.loginservice.security.provider;


import com.example.loginservice.security.auth.SmsAuthenticationToken;
import com.example.loginservice.service.CustomUserDetailsService;
import com.example.loginservice.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 用於處理 SmsAuthenticationToken 的認證提供者。
 */
@Component
@RequiredArgsConstructor
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final SmsService smsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken smsAuthToken = (SmsAuthenticationToken) authentication;

        String phoneNumber = (String) smsAuthToken.getPrincipal();
        String otp = (String) smsAuthToken.getCredentials();

        // 1. 根據電話號碼載入使用者
        UserDetails userDetails;
        try {
            // 注意：這裡假設 loadUserByPhoneNumber 返回的是您的 UserDetails 實現
            userDetails = customUserDetailsService.loadUserByPhoneNumber(phoneNumber);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Phone number not registered.");
        }

        // 2. 驗證 OTP
        if (!smsService.validateOtp(phoneNumber, otp)) {
            throw new BadCredentialsException("Invalid or expired OTP.");
        }

        // 3. 認證成功，返回一個已認證的 Authentication 物件
        // 通常會返回 UsernamePasswordAuthenticationToken，因為它是最通用的 UserDetails 包裝器
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判斷這個 Provider 是否支持 SmsAuthenticationToken 類型
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
