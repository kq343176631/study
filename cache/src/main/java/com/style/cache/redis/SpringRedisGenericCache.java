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
 * Spring Redis GenericCache
 */
public class SpringRedisGenericCache implements Level2Cache {

    private RedisTemplate<String, Serializable> redisTemplate;

    private String namespace = "j2cache_key";

    private String region;

    public SpringRedisGenericCache(String namespace, String region, RedisTemplate<String, Serializable> redisTemplate) {
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
    public void setBytes(String key, byte[] bytes) {
        this.redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection redis) throws DataAccessException {
                redis.set(_key(key), bytes);
                return null;
            }
        });
    }

    @Override
    public void setBytes(Map<String, byte[]> bytes) {
        bytes.forEach(this::setBytes);
    }

    @Override
    public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds) {
        if (timeToLiveInSeconds <= 0L) {
            this.setBytes(key, bytes);
        } else {
            this.redisTemplate.opsForValue().getOperations().execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redis) throws DataAccessException {
                    redis.setEx(_key(key), (long) ((int) timeToLiveInSeconds), bytes);
                    return null;
                }
            });
        }
    }

    @Override
    public void setBytes(Map<String, byte[]> bytes, long timeToLiveInSeconds) {
        bytes.forEach((k, v) -> {
            this.setBytes(k, v, timeToLiveInSeconds);
        });
    }

    @Override
    public byte[] getBytes(String key) {
        return this.redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection redis) throws DataAccessException {
                return redis.get(_key(key));
            }
        });
    }

    @Override
    public List<byte[]> getBytes(Collection<String> keys) {
        return this.redisTemplate.execute(new RedisCallback<List<byte[]>>() {
            @Override
            public List<byte[]> doInRedis(RedisConnection redis) throws DataAccessException {
                byte[][] bytes = keys.stream().map((k) -> {
                    return _key(k);
                }).toArray((x$0) -> {
                    return new byte[x$0][];
                });
                return redis.mGet(bytes);
            }
        });
    }

    @Override
    public void clear() {
        this.keys().stream().forEach((k) -> {
            this.redisTemplate.delete(k);
        });
    }

    @Override
    public void evict(String... keys) {
        for (String key : keys) {
            this.redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redis) throws DataAccessException {
                    return redis.del(_key(key));
                }
            });
        }
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redis) throws DataAccessException {
                return redis.exists(_key(key));
            }
        });
    }

    @Override
    public Collection<String> keys() {
        Set<String> list = this.redisTemplate.keys(this.region);
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

    private byte[] _key(String key) {
        return (this.region + key).getBytes();
    }
}
