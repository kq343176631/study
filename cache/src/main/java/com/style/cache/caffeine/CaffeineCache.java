package com.style.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import net.oschina.j2cache.Level1Cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Caffeine cache
 */
public class CaffeineCache implements Level1Cache {

    private Cache<String, Object> cache;

    private long size;

    private long expire;

    public CaffeineCache(Cache<String, Object> cache, long size, long expire) {
        this.cache = cache;
        this.size = size;
        this.expire = expire;
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void put(Map<String, Object> elements) {
        cache.putAll(elements);
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public Map<String, Object> get(Collection<String> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void evict(String... keys) {
        cache.invalidateAll(Arrays.asList(keys));
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public Collection<String> keys() {
        return cache.asMap().keySet();
    }

    @Override
    public long ttl() {
        return this.expire;
    }

    @Override
    public long size() {
        return this.size;
    }
}
