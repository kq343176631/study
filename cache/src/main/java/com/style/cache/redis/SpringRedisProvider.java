package com.style.cache.redis;

import com.style.utils.SpringUtils;
import net.oschina.j2cache.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Redis Provider
 */
public class SpringRedisProvider implements CacheProvider {

    private String namespace = "j2cache";

    private String storage = "generic";

    private RedisTemplate<String, Serializable> redisTemplate;

    protected ConcurrentHashMap<String, Level2Cache> caches = new ConcurrentHashMap<>();

    public SpringRedisProvider() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Properties props) {
        this.storage = props.getProperty("storage");
        this.namespace = props.getProperty("namespace");
        this.redisTemplate = SpringUtils.getBean(RedisTemplate.class);
    }

    @Override
    public Cache buildCache(String region, CacheExpiredListener listener) {
        Level2Cache cache = this.caches.get(region);
        if (cache == null) {
            synchronized (SpringRedisProvider.class) {
                Level2Cache innerCache = this.caches.get(region);
                if (innerCache == null) {
                    if ("generic".equals(this.storage)) {
                        innerCache = new SpringRedisGenericCache(this.namespace, region, this.redisTemplate);
                    } else {
                        innerCache = new SpringRedisHashCache(this.namespace, region, this.redisTemplate);
                    }
                    this.caches.put(region, innerCache);
                }
                return innerCache;
            }
        }
        return cache;
    }

    @Override
    public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
        return this.buildCache(region, listener);
    }

    @Override
    public Collection<CacheChannel.Region> regions() {
        return Collections.emptyList();
    }

    @Override
    public void stop() {
    }

    @Override
    public String name() {
        return "redis";
    }

    @Override
    public int level() {
        return CacheObject.LEVEL_2;
    }
}
