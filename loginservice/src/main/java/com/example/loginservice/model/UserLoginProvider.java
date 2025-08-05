package com.example.loginservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_providers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider_name", "provider_user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 多個登入提供者可以屬於一個使用者
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 對應的使用者實體

    @Column(name = "provider_name", length = 50, nullable = false)
    private String providerName;

    @Column(name = "provider_user_id", length = 255, nullable = false)
    private String providerUserId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}