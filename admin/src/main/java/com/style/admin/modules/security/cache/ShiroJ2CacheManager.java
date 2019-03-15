package com.style.admin.modules.security.cache;

import com.style.utils.lang.StringUtils;
import net.oschina.j2cache.CacheChannel;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

public class ShiroJ2CacheManager implements CacheManager {

    private CacheChannel channel;

    private String cacheName = "shiro_cache";

    public ShiroJ2CacheManager(CacheChannel channel) {
        this.channel = channel;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        if (StringUtils.isBlank(cacheName)) {
            cacheName = this.cacheName;
        }
        return new ShiroJ2Cache<>(cacheName, this.channel);
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
