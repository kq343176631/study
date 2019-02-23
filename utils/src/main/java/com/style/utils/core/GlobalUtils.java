package com.style.utils.core;

import com.style.utils.io.PropertyUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 全局工具类
 *
 * @version 2018-11-11
 */
public class GlobalUtils {

    private static Logger logger = LoggerFactory.getLogger(GlobalUtils.class);

    private static final String DATA_SOURCE_KEY_PREFIX = "target-datasource";

    private static final String DATA_SOURCE_NAME_KEY_PREFIX = "target-datasource.datasource-name";

    // 缓存全局属性
    private static Map<String, Object> props = MapUtils.newLinkedHashMap();

    /**
     * 获取属性，如果globalProperty中没有，则从PropertiesUtil获取属性值。
     *
     * @param key key
     * @return String
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

    /**
     * 检查系统是否启用动态数据源
     *
     * @return boolean
     */
    public static boolean isEnableDynamicDataSource() {
        String dataSourceNames = GlobalUtils.getProperty(DATA_SOURCE_NAME_KEY_PREFIX);
        return (StringUtils.isNotBlank(dataSourceNames) && dataSourceNames.split(",").length > 1);
    }

    /**
     * 获取默认的数据源名称
     *
     * @return dataSourceName
     */
    public static String getDefaultDataSourceName() {
        String[] dataSourceNames = getProperty(DATA_SOURCE_NAME_KEY_PREFIX).split(",");
        String result = null;
        for (String dataSourceName : dataSourceNames) {
            String dataSourceType = getProperty(DATA_SOURCE_KEY_PREFIX
                    + "."
                    + dataSourceName
                    + "."
                    + "default");
            if ("true".equals(dataSourceType)) {
                result = dataSourceName;
            }
        }
        if (StringUtils.isBlank(result)) {
            //throw new SystemException("默认数据源名称不能为空。。。");
        }
        return result;
    }

    /**
     * 获取目标数据源名称
     *
     * @return targetDataSourceNames 数组的第一个位置放默认数据源，剩下位置放其它数据源。
     */
    public static String[] getTargetDataSourceNames() {
        String[] dataSourceNames = getProperty(DATA_SOURCE_NAME_KEY_PREFIX).split(",");
        String[] targetDataSourceNames = new String[dataSourceNames.length];
        int otherDataSourceIndex = 1;
        for (String dataSourceName : dataSourceNames) {
            String dataSourceType = getProperty(DATA_SOURCE_KEY_PREFIX
                    + "."
                    + dataSourceName
                    + "."
                    + "default");
            if ("true".equals(dataSourceType)) {
                targetDataSourceNames[0] = dataSourceName;
            } else {
                targetDataSourceNames[otherDataSourceIndex] = dataSourceName;
            }

        }
        return targetDataSourceNames;
    }

}
