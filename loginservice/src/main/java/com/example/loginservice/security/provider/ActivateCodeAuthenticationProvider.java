package com.example.loginservice.security.provider;


import com.example.loginservice.common.ResponseCode;
import com.example.loginservice.common.ValidateException;
import com.example.loginservice.model.UnifiedLoginRequest;
import com.example.loginservice.model.User;
import com.example.loginservice.security.auth.ActivateCodeAuthenticationToken;
import com.example.loginservice.security.auth.OptAuthenticationToken;
import com.example.loginservice.security.auth.SmsAuthenticationToken;
import com.example.loginservice.service.CustomUserDetailsService;
import com.example.loginservice.service.EmailService;
import com.example.loginservice.service.SmsService;
import com.example.loginservice.util.PrefixUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 用於處理 SmsAuthenticationToken 的認證提供者。
 */
@Component
@RequiredArgsConstructor
public class ActivateCodeAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OptAuthenticationToken optAuthenticationToken = (OptAuthenticationToken) authentication;
        User userDetails;
        UnifiedLoginRequest request = optAuthenticationToken.getRequest();
        try {

            userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
            if(!userDetails.getPassword().equals(passwordEncoder.encode(request.getPassword()))) {
                throw new ValidateException(ResponseCode.INVALID_LOGIN_REQUEST, "Username or Password not match.");
            }
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Username or Password not match.");
        }

        switch (request.getType()) {
            case 1:
                if(emailService.validateActivationCode(request.getUsername(), optAuthenticationToken.getOtp())) {
                    throw new ValidateException(ResponseCode.INVALID_LOGIN_REQUEST, "Invalid or expired OTP.");
                }
                break;
            case 2:
                if (!smsService.validateOtp(request.getMobilePhoneNumber(), optAuthenticationToken.getOtp())) {
                    throw new ValidateException(ResponseCode.INVALID_LOGIN_REQUEST, "Invalid or expired OTP.");
                }
                break;
            default: throw new ValidateException(ResponseCode.INVALID_LOGIN_REQUEST, "type not found.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判斷這個 Provider 是否支持 SmsAuthenticationToken 類型
        return OptAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
