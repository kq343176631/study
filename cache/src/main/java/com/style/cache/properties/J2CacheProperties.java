package com.style.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 系统缓存属性配置
 */
@Component
@PropertySource("classpath:config/cache.yml")
@ConfigurationProperties(prefix = "spring.cache")
public class J2CacheProperties {

}
