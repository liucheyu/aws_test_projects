package com.example.loginservice.service;

import com.example.loginservice.model.User;
import com.example.loginservice.model.UserLoginProvider;
import com.example.loginservice.repository.UserLoginProviderRepository;
import com.example.loginservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, User> users = new HashMap<>(); // 模擬資料庫使用者
    private final UserLoginProviderRepository userLoginProviderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserLoginProviderRepository userLoginProviderRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userLoginProviderRepository = userLoginProviderRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
//        // 初始化一些模擬使用者
//        users.put("testuser", new User("testuser", passwordEncoder.encode("password"), "test@example.com", "0900000001",Arrays.asList("USER"), "local", null));
//        users.put("admin", new User("admin", passwordEncoder.encode("adminpass"), "admin@example.com", "0900000002",Arrays.asList("USER", "ADMIN"), "local", null));
//    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 對於帳號密碼登入，我們查找 provider_name='local' 且 provider_user_id=username 的記錄
        UserLoginProvider localProvider = userLoginProviderRepository.findByProviderNameAndProviderUserId("local", username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        User user = localProvider.getUser(); // 獲取對應的使用者主體
        user.setPassword(localProvider.getCredentials()); // 將密碼憑證設定回 UserDetails 物件

        return user;
    }

    @Transactional
    public UserDetails loadUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        // 查找 Users 表中對應電話號碼的使用者
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);

        if(byPhoneNumber.isPresent()) {
            return byPhoneNumber.get();
        }

        // 如不存在則新增
        User newUser = new User();
        newUser.setUsername(phoneNumber); // 使用電話號碼作為用戶名
        newUser.setPhoneNumber(phoneNumber);
        newUser.setDisplayName(phoneNumber);
        newUser.addRole("ROLE_USER"); // 為新使用者分配預設角色
        newUser = userRepository.save(newUser); // 儲存新 User
        System.out.println("Created new user for phone number: " + newUser.getUsername());
        return newUser;
    }

    // 可以在這裡添加根據 provider 和 providerId 查找或創建使用者的邏輯
    public UserDetails loadOrCreateOAuth2User(String providerName, String providerUserId, String email, String displayName, String profilePictureUrl) {
        // 1. 嘗試查找現有的登入提供者綁定
        Optional<UserLoginProvider> existingProvider = userLoginProviderRepository.findByProviderNameAndProviderUserId(providerName, providerUserId);

        User user;
        if (existingProvider.isPresent()) {
            // 2. 如果已存在該提供者的綁定，獲取對應的 User
            UserLoginProvider prd= existingProvider.get();
            user = prd.getUser();
            // 可以更新使用者資訊，例如 email 或大頭照
            user.setEmail(email);
            user.setDisplayName(displayName);
            user.setProfilePictureUrl(profilePictureUrl);
            userRepository.save(user); // 更新 User 資訊
            System.out.println("Found existing OAuth2 user: " + user.getUsername() + " from " + providerName);
        } else {
            // 3. 如果該提供者綁定不存在，檢查是否已有相同 email 的使用者 (email 綁定)
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);
            if (existingUserByEmail.isPresent()) {
                // 找到現有使用者，將新的 OAuth2 綁定添加到該使用者
                user = existingUserByEmail.get();
                System.out.println("Found existing user by email, binding new OAuth2 provider: " + providerName);
            } else {
                // 4. 完全新的使用者，創建 User 實體
                user = new User();
                user.setEmail(email);
                user.setUsername(generateUniqueUsername(email)); // 生成一個唯一的用戶名
                user.setDisplayName(displayName != null ? displayName : email.split("@")[0]);
                user.setProfilePictureUrl(profilePictureUrl);
                user.addRole("ROLE_USER"); // 為新使用者分配預設角色
                user = userRepository.save(user); // 儲存新 User
                System.out.println("Created new user for OAuth2: " + user.getUsername());
            }

            // 5. 創建新的 UserLoginProvider 記錄並綁定到 User
            UserLoginProvider newProvider = new UserLoginProvider();
            newProvider.setUser(user);
            newProvider.setProviderName(providerName);
            newProvider.setProviderUserId(providerUserId);
            // newProvider.setCredentials(null); // OAuth2 不需要密碼
            userLoginProviderRepository.save(newProvider);
        }
        return user;
    }

    // 輔助方法：生成唯一的用戶名
    private String generateUniqueUsername(String base) {
        String username = base.split("@")[0];
        int counter = 0;
        while (userRepository.findByUsername(username).isPresent()) {
            username = base.split("@")[0] + "_" + (++counter);
        }
        return username;
    }

    // 輔助方法：新增用戶並綁定本地密碼 (用於註冊)
    @Transactional
    public User registerNewUser(String username, String password, String email, String phoneNumber) {
        // 檢查 email, username, phone 唯一性
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken.");
        }
        if (phoneNumber != null && userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setDisplayName(username); // 預設顯示名稱為用戶名
        newUser.addRole("ROLE_USER");
        newUser = userRepository.save(newUser);

        UserLoginProvider localProvider = new UserLoginProvider();
        localProvider.setUser(newUser);
        localProvider.setProviderName("local");
        localProvider.setProviderUserId(username); // 對於本地登入，provider_user_id 可以是 username
        localProvider.setCredentials(passwordEncoder.encode(password)); // 加密密碼
        userLoginProviderRepository.save(localProvider);

        return newUser;
    }
}