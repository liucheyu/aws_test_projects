package com.example.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.*;

@Configuration
public class SysConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 設定鍵的序列化器：使用 StringRedisSerializer，確保鍵是可讀的字串
        template.setKeySerializer(new StringRedisSerializer());

        // 設定值的序列化器：使用 GenericJackson2JsonRedisSerializer，將 Java 物件序列化為 JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 設定 Hash 類型的鍵和值的序列化器
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 預設快取設定，永不過期
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // 不快取空值
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 快取名稱與其設定的對應關係
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        Set<String> cacheNames = new HashSet<>();
        // 針對設置過期時間
        Arrays.stream(CacheEnum.values()).forEach(cacheEnum -> {
            cacheConfigurations.put(cacheEnum.getPrefix(), defaultCacheConfiguration.entryTtl(Duration.ofMillis(cacheEnum.getMillisecond())));
            cacheNames.add(cacheEnum.getPrefix());
        });

        // 針對 "sessionCache" 這個快取設定 30 分鐘過期
        //cacheConfigurations.put("sessionCache", defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        //cacheNames.add("sessionCache");


        return RedisCacheManager.builder(factory)
                .initialCacheNames(cacheNames) // 預先定義快取名稱
                .withInitialCacheConfigurations(cacheConfigurations) // 設定各快取的過期時間
                .build();
    }
}
