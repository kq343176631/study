package com.style.cache;

import com.style.utils.core.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

/**
 * Caffeine Cache Utils
 * 默认缓存时间：30分钟。
 */
public class CaffeineUtils {

    private static Logger logger = LoggerFactory.getLogger(CaffeineUtils.class);

    private static CacheManager cacheManager = SpringUtils.getBean(CaffeineCacheManager.class);

    private static final String SYS_CACHE = "sys_cache";

    /**
     * 写入SYS_CACHE缓存
     *
     * @param key key
     */
    public static void put(String key, Object value) {
        put(SYS_CACHE, key, value);
    }

    /**
     * 写入缓存
     *
     * @param cacheName cacheName
     * @param key       key
     * @param value     value
     */
    public static void put(String cacheName, String key, Object value) {
        getCache(cacheName).put(key, value);
    }

    /**
     * 获取SYS_CACHE缓存
     *
     * @param key key
     * @return object
     */
    public static Object get(String key) {
        return get(SYS_CACHE, key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName cacheName
     * @param key       key
     * @return object
     */
    public static Object get(String cacheName, String key) {
        return getCache(cacheName).get(key).get();
    }

    /**
     * 从SYS_CACHE缓存中移除
     */
    public static void remove(String key) {
        remove(SYS_CACHE, key);
    }

    /**
     * 从缓存中移除
     *
     * @param cacheName cacheName
     * @param key       key
     */
    public static void remove(String cacheName, String key) {
        getCache(cacheName).evict(key);
    }

    /**
     * 从缓存中移除所有
     *
     * @param cacheName cacheName
     */
    public static void clear(String cacheName) {
        getCache(cacheName).clear();
        logger.info("clear{} => ", cacheName);
    }

    /**
     * 获得一个Cache，没有则显示日志。
     *
     * @param cacheName cacheName
     * @return cache
     */
    private static Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            logger.warn("cache:" + cacheName + "is not exist !!!");
        }
        return cache;
    }
}
