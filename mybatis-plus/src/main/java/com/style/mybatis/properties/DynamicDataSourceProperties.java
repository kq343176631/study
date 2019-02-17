package com.style.mybatis.properties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 多数据源属性
 *
 */
public class DynamicDataSourceProperties {

    private Map<String, DataSourceProperties> datasource = new LinkedHashMap<>();

    public Map<String, DataSourceProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, DataSourceProperties> datasource) {
        this.datasource = datasource;
    }
}
