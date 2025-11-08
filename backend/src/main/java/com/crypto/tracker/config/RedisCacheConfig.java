package com.crypto.tracker.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    // Cache names - used in @Cacheable annotations
    public static final String PRICE_LIST_CACHE = "priceList";
    public static final String CRYPTO_HISTORY_CACHE = "cryptoHistory";

    // TTL values in seconds
    private static final long PRICE_LIST_TTL_SECONDS = 120;  // 2 minutes
    private static final long HISTORY_TTL_SECONDS = 300;      // 5 minutes

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(PRICE_LIST_TTL_SECONDS))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()
                    )
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                    )
                )
                .disableCachingNullValues();  // Don't cache null results

        // Custom configurations for specific caches
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Price list cache: 120 seconds
        cacheConfigurations.put(
            PRICE_LIST_CACHE,
            defaultConfig.entryTtl(Duration.ofSeconds(PRICE_LIST_TTL_SECONDS))
        );

        // Historical data cache: 300 seconds
        cacheConfigurations.put(
            CRYPTO_HISTORY_CACHE,
            defaultConfig.entryTtl(Duration.ofSeconds(HISTORY_TTL_SECONDS))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
