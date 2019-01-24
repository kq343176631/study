package com.style.cache.redis;

import com.style.utils.lang.StringUtils;
import net.oschina.j2cache.Level2Cache;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.*;

/**
 * Spring Redis HashCache
 */
public class SpringRedisHashCache implements Level2Cache {

    private RedisTemplate<String, Serializable> redisTemplate;

    private String namespace = "j2cache_key";

    private String region;

    public SpringRedisHashCache(String namespace, String region, RedisTemplate<String, Serializable> redisTemplate) {
        if (region == null || region.isEmpty()) {
            region = "_";
        }
        if (StringUtils.isNotBlank(namespace)) {
            this.namespace = namespace;
        }
        this.redisTemplate = redisTemplate;
        this.region = getRegionName(region);
    }

    @Override
    public void put(String key, Object value) {
        this.redisTemplate.opsForHash().put(this.region, key, value);
    }

    @Override
    public void put(String key, Object value, long timeToLiveInSeconds) {
        // Hash 不支持 过期时间
        this.put(key, value);
    }

    @Override
    public void setBytes(String key, byte[] bytes) {
        this.redisTemplate.opsForHash().getOperations().execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection redis) throws DataAccessException {
                // redis.set(key.getBytes(), bytes);
                redis.hSet(region.getBytes(), key.getBytes(), bytes);
                return null;
            }
        });
    }

    @Override
    public void setBytes(Map<String, byte[]> bytes) {
        bytes.forEach((k, v) -> {
            this.setBytes(k, v);
        });
    }

    @Override
    public byte[] getBytes(String key) {
        return redisTemplate.opsForHash().getOperations().execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection redis) {
                return redis.hGet(region.getBytes(), key.getBytes());
            }
        });
    }

    @Override
    public List<byte[]> getBytes(Collection<String> keys) {
        return this.redisTemplate.opsForHash().getOperations().execute(new RedisCallback<List<byte[]>>() {
            @Override
            public List<byte[]> doInRedis(RedisConnection redis) throws DataAccessException {
                byte[][] bytes = keys.stream().map((k) -> {
                    return k.getBytes();
                }).toArray((x$0) -> {
                    return new byte[x$0][];
                });
                return redis.hMGet(region.getBytes(), bytes);
            }
        });
    }

    @Override
    public void evict(String... keys) {
        for (String k : keys) {
            if (StringUtils.isNotBlank(k)) {
                redisTemplate.opsForHash().delete(region, k);
            } else {
                this.clear();
            }
        }
    }

    @Override
    public void clear() {
        // this.redisTemplate.delete(this.region);
        this.redisTemplate.opsForHash().delete(this.region);
    }

    @Override
    public boolean exists(String key) {
        return this.redisTemplate.opsForHash().hasKey(this.region, key);
    }

    @Override
    public Collection<String> keys() {
        Set<Object> list = this.redisTemplate.opsForHash().keys(this.region);
        List<String> keys = new ArrayList<>(list.size());
        for (Object object : list) {
            keys.add((String) object);
        }
        return keys;
    }

    private String getRegionName(String region) {
        if (StringUtils.isNotBlank(this.namespace))
            region = this.namespace + ":" + region;
        return region;
    }
}
