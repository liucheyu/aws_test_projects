package com.example.loginservice.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 自定義的 OAuth2User 實現，用於橋接 Spring Security 提供的 OAuth2User
 * 和您應用程式內部的使用者模型。
 * 它包裝了您自己的 User 實體，並代理了原始 OAuth2User 的屬性。
 */
public class CustomOAuth2User implements OAuth2User {

    private final User user; // 您應用程式自己的使用者實體
    private final OAuth2User oauth2User; // 原始的 OAuth2User (來自 Google/Facebook)

    public CustomOAuth2User(User user, OAuth2User oauth2User) {
        this.user = user;
        this.oauth2User = oauth2User;
    }

    // --- 實現 OAuth2User 介面 ---

    @Override
    public Map<String, Object> getAttributes() {
        // 返回原始 OAuth2User 的屬性，這些是從第三方平台獲取的完整屬性
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 返回您應用程式使用者實體（User）的權限
        return user.getAuthorities();
    }

    @Override
    public String getName() {
        // 返回在第三方平台中的唯一標識符
        // oauth2User.getName() 通常返回的是 sub (Google) 或 id (Facebook)
        return oauth2User.getName();
    }

    // --- 可以添加額外的方法來方便訪問您 User 實體的屬性 ---

    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }



    public User getUser() {
        return user;
    }
}
