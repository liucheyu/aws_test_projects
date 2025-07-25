package com.example.loginservice.repository;

import com.example.loginservice.model.UserLoginProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserLoginProviderRepository extends JpaRepository<UserLoginProvider, Long> {
    Optional<UserLoginProvider> findByProviderNameAndProviderUserId(String providerName, String providerUserId);
}