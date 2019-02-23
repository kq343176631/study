package com.style.cache.redis;

import com.style.utils.constant.Constants;
import net.oschina.j2cache.cluster.ClusterPolicy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 监听二缓key失效，主动清除本地缓存
 */
public class SpringRedisActiveMessageListener implements MessageListener {

    private ClusterPolicy clusterPolicy;

    private String namespace;

    SpringRedisActiveMessageListener(ClusterPolicy clusterPolicy, String namespace) {
        this.clusterPolicy = clusterPolicy;
        this.namespace = namespace;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (key == null) {
            return;
        }
        String _namespace = namespace + Constants.COLON;
        if (key.startsWith(_namespace)) {
            String[] k = key.replaceFirst(_namespace, Constants.EMPTY).split(Constants.COLON, 2);
            if (k.length != 2) {
                return;
            }
            clusterPolicy.evict(k[0], k[1]);
        }

    }
}
