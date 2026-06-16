package com.example.bookstore.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Configuration class to customize Spring Cache abstraction using Redis.
 * Implements safeguards against Cache Penetration and Cache Avalanche.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures the RedisCacheManager.
     * Integrates JitterRedisCacheWriter to inject random offsets into TTL (anti Cache Avalanche)
     * and uses JSON serialization for readable redis cache data.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 1. Create the default non-locking cache writer
        RedisCacheWriter defaultWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        // 2. Wrap it with JitterRedisCacheWriter to dynamically inject random offsets to TTLs
        RedisCacheWriter jitterWriter = new JitterRedisCacheWriter(defaultWriter);

        // 3. Configure ObjectMapper with JavaTimeModule (for LocalDate/LocalDateTime serialization) and Default Typing
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();
        // Use property-based default typing ("@class": "...") to ensure compatibility with lists and generic types
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 4. Configure default settings (Using JSON for value serialization, enabling null values)
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Default TTL is 10 minutes
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
                // Note: We do not call .disableCachingNullValues() so that null values are stored (anti Cache Penetration).

        return RedisCacheManager.builder(jitterWriter)
                .cacheDefaults(config)
                .build();
    }

    /**
     * Inner decorator for RedisCacheWriter that dynamically adds a random jitter 
     * (between 60 to 300 seconds) to cache write TTLs, protecting the database 
     * from simultaneous cache invalidation storms (Cache Avalanche).
     */
    private static class JitterRedisCacheWriter implements RedisCacheWriter {
        private final RedisCacheWriter delegate;

        public JitterRedisCacheWriter(RedisCacheWriter delegate) {
            this.delegate = delegate;
        }

        @Override
        public void put(String name, byte[] key, byte[] value, Duration ttl) {
            // Append random offset of 1 to 5 minutes to prevent simultaneous expiration
            long jitterSeconds = ThreadLocalRandom.current().nextLong(60, 300);
            Duration finalTtl = (ttl != null) ? ttl.plusSeconds(jitterSeconds) : Duration.ofMinutes(10).plusSeconds(jitterSeconds);
            delegate.put(name, key, value, finalTtl);
        }

        @Override
        public CompletableFuture<Void> store(String name, byte[] key, byte[] value, Duration ttl) {
            // Append random offset of 1 to 5 minutes to prevent simultaneous expiration
            long jitterSeconds = ThreadLocalRandom.current().nextLong(60, 300);
            Duration finalTtl = (ttl != null) ? ttl.plusSeconds(jitterSeconds) : Duration.ofMinutes(10).plusSeconds(jitterSeconds);
            return delegate.store(name, key, value, finalTtl);
        }

        @Override
        public byte[] get(String name, byte[] key) {
            return delegate.get(name, key);
        }

        @Override
        public CompletableFuture<byte[]> retrieve(String name, byte[] key, Duration leaseTime) {
            return delegate.retrieve(name, key, leaseTime);
        }

        @Override
        public byte[] putIfAbsent(String name, byte[] key, byte[] value, Duration ttl) {
            long jitterSeconds = ThreadLocalRandom.current().nextLong(60, 300);
            Duration finalTtl = (ttl != null) ? ttl.plusSeconds(jitterSeconds) : Duration.ofMinutes(10).plusSeconds(jitterSeconds);
            return delegate.putIfAbsent(name, key, value, finalTtl);
        }

        @Override
        public void remove(String name, byte[] key) {
            delegate.remove(name, key);
        }

        @Override
        public void clean(String name, byte[] pattern) {
            delegate.clean(name, pattern);
        }

        @Override
        public CacheStatistics getCacheStatistics(String cacheName) {
            return delegate.getCacheStatistics(cacheName);
        }

        @Override
        public void clearStatistics(String cacheName) {
            delegate.clearStatistics(cacheName);
        }

        @Override
        public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector collector) {
            return new JitterRedisCacheWriter(delegate.withStatisticsCollector(collector));
        }
    }
}
