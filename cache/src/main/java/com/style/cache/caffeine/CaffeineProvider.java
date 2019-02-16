package com.style.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.style.common.lang.StringUtils;
import net.oschina.j2cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache provider
 */
public class CaffeineProvider implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(CaffeineProvider.class);

    private final static String DEFAULT_REGION = "default";

    private ConcurrentHashMap<String, CaffeineCache> caches = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();

    public CaffeineProvider() {

    }

    @Override
    public void start(Properties props) {
        for (String region : props.stringPropertyNames()) {
            String s_config = props.getProperty(region).trim();
            this.saveCacheConfig(region, s_config);
        }
    }

    @Override
    public Cache buildCache(String region, CacheExpiredListener listener) {
        return caches.computeIfAbsent(region, v -> {
            CacheConfig config = cacheConfigs.get(region);
            if (config == null) {
                config = cacheConfigs.get(DEFAULT_REGION);
                if (config == null)
                    throw new CacheException("Undefined [default] caffeine cache");
                log.warn(String.format("Caffeine cache [%s] not defined, using default.", region));
            }
            return newCaffeineCache(region, config.initialCapacity, config.maximumSize, config.expireAfterWrite, listener);
        });
    }

    @Override
    public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
        CaffeineCache cache = caches.computeIfAbsent(region, v -> {
            CacheConfig config = cacheConfigs.get(region);
            if (config != null && config.expireAfterWrite != timeToLiveInSeconds)
                throw new IllegalArgumentException(String.format("Region [%s] TTL %d not match with %d", region, config.expireAfterWrite, timeToLiveInSeconds));

            if (config == null) {
                config = cacheConfigs.get(DEFAULT_REGION);
                if (config == null)
                    throw new CacheException(String.format("Undefined caffeine cache region name = %s", region));
            }

            log.info(String.format("Started caffeine region [%s] with TTL: %d", region, timeToLiveInSeconds));
            return newCaffeineCache(region, config.initialCapacity, config.maximumSize, timeToLiveInSeconds, listener);
        });

        if (cache != null && cache.ttl() != timeToLiveInSeconds)
            throw new IllegalArgumentException(String.format("Region [%s] TTL %d not match with %d", region, cache.ttl(), timeToLiveInSeconds));

        return cache;
    }

    /**
     * 返回对 Caffeine cache 的 封装
     *
     * @param region           region name
     * @param maximumSize      max cache object size in memory
     * @param expireAfterWrite cache object expire time in millisecond
     * @param listener         j2cache cache listener
     * @return CaffeineCache
     */
    private CaffeineCache newCaffeineCache(String region, int initialCapacity, long maximumSize, long expireAfterWrite, CacheExpiredListener listener) {
        com.github.benmanes.caffeine.cache.Cache<String, Object> loadingCache = Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS)
                .removalListener((k, v, cause) -> {
                    //程序删除的缓存不做通知处理，因为上层已经做了处理
                    if (cause != RemovalCause.EXPLICIT && cause != RemovalCause.REPLACED)
                        listener.notifyElementExpired(region, (String) k);
                })
                .build();
        return new CaffeineCache(loadingCache, maximumSize, expireAfterWrite);
    }

    @Override
    public Collection<CacheChannel.Region> regions() {
        Collection<CacheChannel.Region> regions = new ArrayList<>();
        caches.forEach((k, c) -> regions.add(new CacheChannel.Region(k, c.size(), c.ttl())));
        return regions;
    }

    @Override
    public void removeCache(String region) {
        cacheConfigs.remove(region);
        caches.remove(region);
    }

    @Override
    public void stop() {
        caches.clear();
        cacheConfigs.clear();
    }

    @Override
    public String name() {
        return "caffeine";
    }

    @Override
    public int level() {
        return CacheObject.LEVEL_1;
    }

    private void saveCacheConfig(String region, String region_config) {
        CacheConfig cfg = CacheConfig.parse(region_config);
        if (cfg == null)
            log.warn(String.format("Illegal caffeine cache config [%s=%s]", region, region_config));
        else
            cacheConfigs.put(region, cfg);
    }

    /**
     * 缓存配置类
     */
    private static class CacheConfig {

        private int initialCapacity = 0;

        private long maximumSize = 0L;

        private long expireAfterWrite = 0L;

        public static CacheConfig parse(String cfg) {

            CacheConfig cacheConfig = null;

            if (StringUtils.isNotBlank(cfg)) {
                cacheConfig = new CacheConfig();
                for (String props : cfg.split(",")) {
                    if (props.contains("initialCapacity")) {
                        cacheConfig.initialCapacity = Integer.parseInt(props.split("=")[1]);
                    } else if (props.contains("maximumSize")) {
                        cacheConfig.maximumSize = Long.parseLong(props.split("=")[1]);
                    } else if (props.contains("expireAfterWrite")) {
                        String sExpire = props.split("=")[1].trim();
                        char unit = Character.toLowerCase(sExpire.charAt(sExpire.length() - 1));
                        cacheConfig.expireAfterWrite = Long.parseLong(sExpire.substring(0, sExpire.length() - 1));
                        switch (unit) {
                            case 's'://seconds
                                break;
                            case 'm'://minutes
                                cacheConfig.expireAfterWrite *= 60;
                                break;
                            case 'h'://hours
                                cacheConfig.expireAfterWrite *= 3600;
                                break;
                            case 'd'://days
                                cacheConfig.expireAfterWrite *= 86400;
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown expire unit:" + unit);
                        }
                    }
                }
            }
            return cacheConfig;
        }

        @Override
        public String toString() {
            return String.format("[InitialCapacity:%d,MaximumSize:%d,ExpireAfterWrite:%d]",
                    initialCapacity,
                    maximumSize,
                    expireAfterWrite);
        }
    }
}

