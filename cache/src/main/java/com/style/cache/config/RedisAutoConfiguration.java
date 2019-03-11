package com.style.cache.config;

import com.style.cache.j2cache.J2CacheSerializer;
import com.style.utils.collect.ListUtils;
import com.style.utils.core.GlobalUtils;
import com.style.utils.io.PropertyUtils;
import com.style.utils.lang.StringUtils;
import net.oschina.j2cache.J2CacheConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Redis Auto Configuration
 */
@Configuration
public class RedisAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RedisAutoConfiguration() {

    }

    /**
     * 缓存配置对象
     */
    @Bean("j2CacheConfig")
    public J2CacheConfig j2CacheConfig() {
        return getJ2CacheConfig();
    }

    /**
     * Redis Connection Factory
     */
    @Bean("redisConnectionFactory")
    @Primary
    @DependsOn("j2CacheConfig")
    public RedisConnectionFactory redisConnectionFactory(J2CacheConfig j2CacheConfig) {
        Properties props = j2CacheConfig.getL2CacheProperties();
        // 初始化连接池
        LettucePoolingClientConfigurationBuilder lettucePoolConfigBuilder = LettucePoolingClientConfiguration.builder();
        lettucePoolConfigBuilder.commandTimeout(Duration.ofMillis(5000));
        lettucePoolConfigBuilder.poolConfig(this.getGenericRedisPool(props));
        LettuceClientConfiguration configuration = lettucePoolConfigBuilder.build();
        // 构建连接工厂
        return this.initConnectionFactory(configuration, props);
    }

    /**
     * 缓存对象序列化器
     */
    @Bean("j2CacheValueSerializer")
    public RedisSerializer<Object> j2CacheSerializer() {
        return new J2CacheSerializer();
    }

    /**
     * Redis Template
     */
    @Bean("redisTemplate")
    @Primary
    @DependsOn({"redisConnectionFactory", "j2CacheValueSerializer"})
    public RedisTemplate<String, Serializable> redisTemplate(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
            @Qualifier("j2CacheValueSerializer") RedisSerializer<Object> j2CacheValueSerializer) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(j2CacheValueSerializer);
        template.setHashValueSerializer(j2CacheValueSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * j2CacheRedisMessageListenerContainer
     */
    @Bean("j2CacheRedisMessageListenerContainer")
    @DependsOn("redisConnectionFactory")
    RedisMessageListenerContainer container(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }

    /**
     * 根据模式选择不同的连接工厂
     */
    private LettuceConnectionFactory initConnectionFactory(LettuceClientConfiguration configuration, Properties props) {
        // 解析REDIS连接密码
        String password = props.getProperty("password");
        RedisPassword redisPassword;
        if (StringUtils.isNotBlank(password)) {
            redisPassword = RedisPassword.of(password);
        } else {
            redisPassword = RedisPassword.none();
        }
        int database = 0;
        if (StringUtils.isNotBlank(props.getProperty("database"))) {
            database = Integer.parseInt(props.getProperty("database"));
        }
        // 解析集群节点
        List<RedisNode> nodes = this.getRedisNodes(props.getProperty("hosts"));
        // 解析集群模式
        String redisMode = props.getProperty("mode");
        if ("sentinel".equals(redisMode)) {
            RedisSentinelConfiguration sentinel = new RedisSentinelConfiguration();
            sentinel.setDatabase(database);
            sentinel.setPassword(redisPassword);
            sentinel.setSentinels(nodes);
            sentinel.setMaster(props.getProperty("sentinelMasterId"));
            return new LettuceConnectionFactory(sentinel, configuration);
        } else if ("cluster".equals(redisMode)) {
            RedisClusterConfiguration cluster = new RedisClusterConfiguration();
            cluster.setClusterNodes(nodes);
            cluster.setMaxRedirects(3);
            cluster.setPassword(redisPassword);
            return new LettuceConnectionFactory(cluster, configuration);

        } else if ("shared".equals(redisMode)) {
            throw new IllegalArgumentException("Lettuce not support use mode [shared]!!");
        } else {
            if (!"single".equalsIgnoreCase(redisMode)) {
                logger.warn("Redis mode [" + redisMode + "] not defined. Using 'single'.");
            }
            RedisNode node = nodes.get(0);
            RedisStandaloneConfiguration single = new RedisStandaloneConfiguration(node.getHost(), node.getPort());
            single.setDatabase(database);
            single.setPassword(redisPassword);
            return new LettuceConnectionFactory(single, configuration);

        }
    }

    /**
     * 解析集群节点
     */
    private List<RedisNode> getRedisNodes(String hosts) {
        List<RedisNode> nodes = ListUtils.newArrayList();
        for (String nodeStr : hosts.split(",")) {
            String[] nodeArray = nodeStr.split(":");
            String host = nodeArray[0];
            int port = (nodeArray.length > 1) ? Integer.parseInt(nodeArray[1]) : 6379;
            RedisNode node = new RedisNode(host, port);
            nodes.add(node);
        }
        return nodes;
    }

    /**
     * 通用的连接池配置
     */
    private GenericObjectPoolConfig getGenericRedisPool(Properties props) {

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setMaxTotal(Integer.valueOf(props.getProperty("pool.maxTotal")));
        config.setMaxIdle(Integer.valueOf(props.getProperty("pool.maxIdle")));
        config.setMaxWaitMillis(Integer.valueOf(props.getProperty("pool.maxWaitMillis")));

        config.setMinEvictableIdleTimeMillis(Integer.valueOf(props.getProperty("pool.minEvictableIdleTimeMillis")));
        config.setMinIdle(Integer.valueOf(props.getProperty("pool.minIdle")));
        config.setNumTestsPerEvictionRun(Integer.valueOf(props.getProperty("pool.numTestsPerEvictionRun")));

        config.setLifo(Boolean.valueOf(props.getProperty("pool.lifo")));
        config.setSoftMinEvictableIdleTimeMillis(Integer.valueOf(props.getProperty("pool.softMinEvictableIdleTimeMillis")));
        config.setTestOnBorrow(Boolean.valueOf(props.getProperty("pool.testOnBorrow")));

        config.setTestOnReturn(Boolean.valueOf(props.getProperty("pool.testOnReturn")));
        config.setTestWhileIdle(Boolean.valueOf(props.getProperty("pool.testWhileIdle")));
        config.setTimeBetweenEvictionRunsMillis(Integer.valueOf(props.getProperty("pool.timeBetweenEvictionRunsMillis")));

        config.setBlockWhenExhausted(Boolean.valueOf(props.getProperty("pool.blockWhenExhausted")));
        config.setJmxEnabled(Boolean.valueOf(props.getProperty("pool.jmxEnabled")));
        return config;
    }

    /**
     * 初始化j2cache配置
     */
    public static J2CacheConfig getJ2CacheConfig() {
        J2CacheConfig config = new J2CacheConfig();
        config.setSerialization(GlobalUtils.getProperty("j2cache.serialization"));
        config.setBroadcast(GlobalUtils.getProperty("j2cache.broadcast.provider_class"));
        config.setL1CacheName(GlobalUtils.getProperty("j2cache.l1_cache.provider_class"));
        config.setL2CacheName(GlobalUtils.getProperty("j2cache.l2_cache.provider_class"));
        config.setSyncTtlToRedis(!"false".equalsIgnoreCase(GlobalUtils.getProperty("j2cache.sync_ttl_to_redis")));
        config.setDefaultCacheNullObject("true".equalsIgnoreCase(GlobalUtils.getProperty("j2cache.default_cache_null_object")));
        // 分离配置属性
        final String bd_config_section = GlobalUtils.getProperty("j2cache.broadcast.config_section");
        final String l1_config_section = GlobalUtils.getProperty("j2cache.l1_cache.config_section");
        final String l2_config_section = GlobalUtils.getProperty("j2cache.l2_cache.config_section");
        Properties properties = PropertyUtils.getInstance().getProperties();
        properties.forEach((k, v) -> {
            String key = (String) k;
            // 组播配置属性
            if (key.startsWith(bd_config_section + ".")) {
                config.getBroadcastProperties().setProperty(key.substring((bd_config_section + ".").length()), (String) v);
            }
            // 一级缓存属性
            if (key.startsWith(l1_config_section + ".")) {
                config.getL1CacheProperties().setProperty(key.substring((l1_config_section + ".").length()), (String) v);
            }
            // 二级缓存属性
            if (key.startsWith(l2_config_section + ".")) {
                config.getL2CacheProperties().setProperty(key.substring((l2_config_section + ".").length()), (String) v);
            }
        });
        return config;
    }
}
