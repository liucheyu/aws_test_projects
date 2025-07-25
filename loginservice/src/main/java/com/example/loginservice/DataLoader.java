package com.example.loginservice;

import com.example.loginservice.model.User; // 假設您有 User 實體
import com.example.loginservice.service.CustomUserDetailsService; // 假設您的 UserDetailsService 負責儲存用戶
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private final CustomUserDetailsService customUserDetailsService; // 注入您的服務
    private final PasswordEncoder passwordEncoder;

    public DataLoader(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 這裡載入測試數據
        // 請注意，如果您的 CustomUserDetailsService 已經有初始數據，這裡可能不需要重複

//        if (customUserDetailsService.loadUserByUsername("testuser") == null) { // 避免重複創建
//            User user1 = new User("testuser", passwordEncoder.encode("password"), "test@example.com", "",Arrays.asList("USER"), "local", null);
//            user1.setPhoneNumber("0912345678");
//            // 這裡假設 CustomUserDetailsService 有一個儲存 User 的方法
//            // customUserDetailsService.saveUser(user1); // 您需要自行添加此方法
//            System.out.println("Loaded testuser: " + user1.getUsername());
//        }
//
//        if (customUserDetailsService.loadUserByUsername("admin") == null) {
//            User adminUser = new User("admin", passwordEncoder.encode("adminpass"), "admin@example.com", "",Arrays.asList("USER", "ADMIN"), "local", null);
//            adminUser.setPhoneNumber("0987654321");
//            // customUserDetailsService.saveUser(adminUser); // 您需要自行添加此方法
//            System.out.println("Loaded admin: " + adminUser.getUsername());
//        }

        // ... 您可以在這裡添加更多的測試數據
    }
}