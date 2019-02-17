package com.style.mybatis.plugin.dynamic;

import com.style.common.lang.StringUtils;

/**
 * 本地数据源持有者
 */
public class DataSourceHolder {

    /**
     * 定义默认数据源
     */
    private static final String DEFAULT_DATA_SOURCE_NAME = "default";

    /**
     * 使用本地变量保存 dataSourceName
     */
    private static final ThreadLocal<String> dataSourceName = new ThreadLocal<>();

    /**
     * 从本地线程中获取 dataSourceName
     *
     * @return dataSourceName
     */
    public static String getCurDataSourceName() {
        return dataSourceName.get();
    }

    /**
     * 设置 dataSourceName
     *
     * @param dataSourceName dataSourceName
     */
    public static void setDataSourceName(String dataSourceName) {
        // 判断dataSourceName是否为空以及是否为默认值
        if (StringUtils.isNotBlank(dataSourceName) && !dataSourceName.equals(getDefaultDataSourceName())) {
            DataSourceHolder.dataSourceName.set(dataSourceName);
        } else {
            clearDataSourceName();
        }
    }

    /**
     * 清空 dataSourceName
     */
    private static void clearDataSourceName() {
        dataSourceName.remove();
    }

    /**
     * 获取默认的数据源名称
     *
     * @return defaultDataSourceName
     */
    public static String getDefaultDataSourceName() {
        return DEFAULT_DATA_SOURCE_NAME;
    }
}
