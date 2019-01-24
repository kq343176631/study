package com.style.cache.j2cache;

import net.oschina.j2cache.CacheChannel;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Spring J2Cache Manager
 */
public class SpringJ2CacheManager extends AbstractTransactionSupportingCacheManager {

    private boolean allowNullValues = true;

    private Collection<String> cacheNames;

    private boolean dynamic = true;

    private CacheChannel channel;

    public SpringJ2CacheManager(CacheChannel channel) {
        this.channel = channel;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Collection<Cache> caches = new LinkedHashSet<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            caches.add(new SpringJ2Cache(cacheName, channel, allowNullValues));
        }
        return caches;
    }

    @Override
    protected Cache getMissingCache(String cacheName) {
        return this.dynamic ? new SpringJ2Cache(cacheName, channel, allowNullValues) : null;
    }

    /**
     * 设置缓存名称
     *
     * @param cacheNames cacheNames
     */
    public void setCacheNames(Collection<String> cacheNames) {
        Set<String> newCacheNames = CollectionUtils.isEmpty(cacheNames) ? Collections.emptySet()
                : new HashSet<>(cacheNames);
        this.cacheNames = newCacheNames;
        this.dynamic = newCacheNames.isEmpty();
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }
}
