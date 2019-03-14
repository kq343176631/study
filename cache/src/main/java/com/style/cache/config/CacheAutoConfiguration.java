package com.style.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.style.cache.j2cache.SpringJ2CacheManager;
import com.style.utils.core.GlobalUtils;
import com.style.utils.lang.ArrayUtils;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@ConditionalOnClass({J2Cache.class})
public class CacheAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    public CacheAutoConfiguration() {

    }

    /**
     * 缓存高速通道
     */
    @Bean("cacheChannel")
    @DependsOn({"springUtils", "redisTemplate", "j2CacheConfig", "j2CacheRedisMessageListenerContainer"})
    public CacheChannel cacheChannel(J2CacheConfig j2CacheConfig) {
        return J2CacheBuilder.init(j2CacheConfig).getChannel();
    }

    /**
     * 本地缓存管理器
     *
     * @return caffeineCacheManager
     */
    @Bean("caffeineCacheManager")
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        List<String> cacheNames = ArrayUtils.toList(
                GlobalUtils.getProperty("j2cache.cache_names").split(".")
        );
        caffeineCacheManager.setCaffeine(Caffeine.from(CaffeineSpec.parse(
                GlobalUtils.getProperty("caffeine.default")
        )));
        caffeineCacheManager.setCacheNames(cacheNames);
        caffeineCacheManager.setAllowNullValues(true);
        return caffeineCacheManager;
    }

    /**
     * Redis缓存管理器
     */
    @Bean("redisCacheManager")
    @DependsOn({"redisConnectionFactory", "j2CacheValueSerializer"})
    public RedisCacheManager redisCacheManager(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
            @Qualifier("j2CacheValueSerializer") RedisSerializer<Object> j2CacheValueSerializer) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 默认缓存过期时间为60分钟。
        config.entryTtl(Duration.ofMinutes(Integer.valueOf(GlobalUtils.getProperty("redis.expire"))));

        // 设置 Key 序列化器
        config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        // 设置 Value 序列化器
        config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(j2CacheValueSerializer));

        // 创建缓存管理器
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory), config);
    }

    /**
     * 两级缓存管理
     */
    @Bean("springJ2CacheManager")
    @Primary
    @DependsOn("cacheChannel")
    public SpringJ2CacheManager springJ2CacheManager(CacheChannel cacheChannel) {
        List<String> cacheNames = ArrayUtils.toList(
                GlobalUtils.getProperty("j2cache.cache_names").split(".")
        );
        SpringJ2CacheManager j2CacheManager = new SpringJ2CacheManager(cacheChannel);
        j2CacheManager.setCacheNames(cacheNames);
        j2CacheManager.setAllowNullValues(true);
        logger.warn("setCacheNames{},not dynamic create cache", cacheNames);
        return j2CacheManager;
    }

    /*@Bean("shiroCacheManager")
    @DependsOn("cacheChannel")
    public CacheManager ShiroJ2CacheManager(CacheChannel cacheChannel) {
        return new ShiroJ2CacheManager(cacheChannel);
    }*/
}
