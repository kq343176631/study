package com.style.cache.j2cache;

import com.style.common.lang.StringUtils;
import net.oschina.j2cache.CacheChannel;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;

/**
 * Spring J2Cache
 */
public class SpringJ2Cache extends AbstractValueAdaptingCache {

    private String region;

    private CacheChannel channel;

    public SpringJ2Cache(String cacheName, CacheChannel channel, boolean allowNullValues) {
        super(allowNullValues);
        if (StringUtils.isBlank(cacheName)) {
            this.region = "sys_cache";
        }
        this.region = cacheName;
        this.channel = channel;
    }

    @Override
    public void put(Object key, Object value) {
        channel.set(region, String.valueOf(key), value);
    }

    @Override
    protected Object lookup(Object key) {
        return channel.get(region, String.valueOf(key)).getValue();
    }

    @Override
    public void evict(Object key) {
        channel.evict(region, String.valueOf(key));
    }

    @Override
    public void clear() {
        channel.clear(region);
    }

    @Override
    public String getName() {
        return this.region;
    }

    @Override
    public Object getNativeCache() {
        return this.channel;
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (!channel.exists(region, String.valueOf(key))) {
            put(key, value);
        }
        return get(key);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value;
        try {
            value = valueLoader.call();
        } catch (Exception ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
        put(key, value);
        return value;
    }

    public void setCacheName(String cacheName) {
        this.region = cacheName;
    }

}
