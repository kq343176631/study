package com.style.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    public static DataSource buildDataSource(DynamicDataSourceProperties properties) {
        if (properties != null && properties.getDatasource().size() > 0) {
            return new DynamicDataSource();
        }
        return new DruidDataSource();
    }

    public static DynamicDataSource buildDynamicDataSource(){

        return null;
    }
}
