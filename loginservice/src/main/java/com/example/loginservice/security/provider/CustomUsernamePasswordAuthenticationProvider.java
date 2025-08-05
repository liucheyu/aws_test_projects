package com.example.loginservice.security.provider;

import com.example.loginservice.model.User;
import com.example.loginservice.repository.UserRepository;
import com.example.loginservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 從 Authentication 物件中取得用戶名和密碼
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 1. 調用您的 DAO (或 Service) 查找用戶
        User user = userService.loadUserByUsername(username);

        // 2. 驗證密碼
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 3. 如果驗證成功，返回一個已認證的 Authentication 物件
            // 在這裡可以加入用戶的角色和權限
            return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        } else {
            // 4. 如果驗證失敗，拋出異常
            throw new BadCredentialsException("Invalid username or password");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 確保這個 Provider 只處理 UsernamePasswordAuthenticationToken 類型
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}