package com.style.admin.modules.security.cache;

import com.style.utils.collect.ListUtils;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.*;

/**
 * Shiro J2 Cache
 */
public class ShiroJ2Cache<K, V> implements Cache<K, V> {

    // 缓存名字
    private String region;

    // 高速缓存通道
    private CacheChannel channel;

    public ShiroJ2Cache(String region, CacheChannel channel) {
        this.region = region;
        this.channel = channel;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        this.channel.set(this.region, key.toString(), value);
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) throws CacheException {
        CacheObject val = this.channel.get(this.region, key.toString());
        if (val == null) {
            return null;
        }
        return (V) val.getValue();
    }


    @Override
    public V remove(K key) throws CacheException {
        this.channel.evict(this.region, key.toString());
        return null;
    }

    @Override
    public void clear() throws CacheException {
        this.channel.clear(this.region);
    }

    @Override
    public int size() {
        return this.channel.keys(this.region).size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        return new HashSet<>((Collection) this.channel.keys(this.region));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        List list = ListUtils.newArrayList();
        Collection keys = this.keys();
        Map<String, CacheObject> cacheMap;
        if ((cacheMap = this.channel.get(this.region, keys)) != null) {
            cacheMap.forEach((key, value) -> list.add(value.getValue()));
        }
        return list;
    }
}
