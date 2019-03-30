package com.style.utils.core;

import com.style.utils.collect.MapUtils;
import com.style.utils.io.PropertyUtils;
import com.style.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 全局工具类
 */
public class GlobalUtils {

    private static Logger logger = LoggerFactory.getLogger(GlobalUtils.class);

    // 缓存全局属性
    private static Map<String, Object> props = MapUtils.newLinkedHashMap();

    /**
     * 获取属性，如果globalProperty中没有，则从PropertiesUtil获取属性值。
     */
    public static String getProperty(String key) {
        String value = null;
        if (StringUtils.isNotBlank(key)) {
            if ((value = (String) props.get(key)) == null) {
                // 从PropertiesUtil拿取属性
                if ((value = PropertyUtils.getInstance().getProperty(key)) != null) {
                    // 缓存到globalProperty中
                    props.put(key, value);
                    return value;
                }
            }
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

}
