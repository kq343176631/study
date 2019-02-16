package com.style.cache.redis;

import com.style.common.SpringUtils;
import com.style.common.lang.ListUtils;
import com.style.common.lang.StringUtils;
import net.oschina.j2cache.Command;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.cluster.ClusterPolicy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.io.Serializable;
import java.util.Properties;

/**
 * 集群策略（广播策略）
 */
public class SpringRedisPubSubPolicy implements ClusterPolicy {

    private static boolean isActive = false;

    private String cacheCleanMode = "passive";

    private String channelName = "j2cache_channel";

    private RedisTemplate<String, Serializable> redisTemplate;

    public SpringRedisPubSubPolicy() {
        J2CacheConfig j2config = SpringUtils.getBean(J2CacheConfig.class);
        this.initProperties(j2config.getBroadcastProperties());
        this.initRedisMessageListener(j2config.getL2CacheProperties());
    }

    /**
     * 初始化订阅消息监听器
     */
    @SuppressWarnings("unchecked")
    private void initProperties(Properties BroadcastProperties) {

        this.redisTemplate = SpringUtils.getBean(RedisTemplate.class);
        // 缓存清理模式
        String cacheCleanMode = BroadcastProperties.getProperty("cache_clean_mode");
        if (StringUtils.isNotBlank(cacheCleanMode)) {
            this.cacheCleanMode = cacheCleanMode;
        }
        // 是否主动清楚
        if ("active".equals(this.cacheCleanMode)) {
            SpringRedisPubSubPolicy.isActive = true;
        }
        // 广播通道名称
        String channelName = BroadcastProperties.getProperty("channel.name");
        if (StringUtils.isNotBlank(channelName)) {
            this.channelName = channelName;
        }
    }

    /**
     * 初始化订阅消息监听器
     */
    private void initRedisMessageListener(Properties l2CacheProperties) {
        // 订阅信息消息监听器
        RedisMessageListenerContainer container = SpringUtils.getBean(RedisMessageListenerContainer.class);
        // 监听J2Cache广播通道
        container.addMessageListener(
                new SpringRedisMessageListener(this, this.channelName),
                new PatternTopic(this.channelName)
        );
        // 监听二级缓存的 Key 是否失效，主动清除本地缓存
        if (isActive || "blend".equals(this.cacheCleanMode)) {
            // 设置 键值 回调
            new ConfigureNotifyKeyspaceEventsAction(
                    container.getConnectionFactory().getConnection()
            );
            // 二级缓存监听器
            String namespace = l2CacheProperties.getProperty("namespace");
            String database = l2CacheProperties.getProperty("database");
            if (StringUtils.isBlank(database)) {
                database = "0";
            }
            String expired = new StringBuilder().insert(0, "__keyevent@")
                    .append(database)
                    .append("__:expired")
                    .toString();
            String del = new StringBuilder().insert(0, "__keyevent@")
                    .append(database)
                    .append("__:del")
                    .toString();
            container.addMessageListener(
                    new SpringRedisActiveMessageListener(this, namespace),
                    ListUtils.newArrayList(
                            new PatternTopic(expired),
                            new PatternTopic(del)
                    )
            );
        }
    }

    /**
     * 连接集群：在实例化当前对象后，立刻调用该方法。
     *
     * @param props BroadcastProperties
     */
    @Override
    public void connect(Properties props) {

    }

    @Override
    public void publish(Command cmd) {
        if (!isActive || "blend".equals(this.cacheCleanMode)) {
            redisTemplate.convertAndSend(this.channelName, cmd.json());
        }
    }

    @Override
    public void disconnect() {
        redisTemplate.convertAndSend(this.channelName, Command.quit().json());
    }

    @Override
    public void evict(String region, String... keys) {
        if (!isActive || "blend".equals(this.cacheCleanMode)) {
            String com = new Command(Command.OPT_EVICT_KEY, region, keys).json();
            redisTemplate.convertAndSend(this.channelName, com);
        }
    }

    @Override
    public void clear(String region) {
        if (!isActive || "blend".equals(this.cacheCleanMode)) {
            String com = new Command(Command.OPT_CLEAR_KEY, region, "").json();
            redisTemplate.convertAndSend(this.channelName, com);
        }
    }

    @Override
    public void handleCommand(Command cmd) {
        redisTemplate.convertAndSend(this.channelName, Command.quit().json());
    }
}
