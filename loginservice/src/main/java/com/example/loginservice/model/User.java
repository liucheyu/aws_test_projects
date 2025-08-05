package com.example.loginservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime; // 導入 LocalDateTime
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 使用 Long 對應 BIGINT

    @Column
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String username; // 可選

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "profile_picture_url", length = 2048)
    private String profilePictureUrl;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ElementCollection(fetch = FetchType.EAGER) // 立即載入角色
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    private List<String> roles;

    @PrePersist // 在儲存前設定建立時間
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // 在更新前設定更新時間
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- UserDetails 介面實現 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        // 實際密碼將從 UserLoginProvider 中載入，這裡返回 null 或從該使用者的 'local' provider 獲取
        // 這需要在 CustomUserDetailsService 處理時正確設置
        return this.password;
    }

    @Override
    public String getUsername() {
        // 根據您的應用程式設計，可以返回 email 或 username
        // 在這裡，我們將 username 作為 principal
        return this.username != null ? this.username : this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return isActive; } // 簡單假設啟用即未過期
    @Override
    public boolean isAccountNonLocked() { return isActive; }  // 簡單假設啟用即未鎖定
    @Override
    public boolean isCredentialsNonExpired() { return isActive; } // 簡單假設啟用即憑證未過期
    @Override
    public boolean isEnabled() { return isActive; } // 簡單假設啟用即啟用

    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = List.of(role);
        } else {
            this.roles.add(role);
        }

    }
}