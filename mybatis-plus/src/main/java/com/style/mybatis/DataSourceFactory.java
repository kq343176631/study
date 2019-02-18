package com.style.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosSQLException;
import com.style.mybatis.plugin.dynamic.DataSourceHolder;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import com.style.mybatis.properties.DataSourceProperties;
import com.style.mybatis.properties.DynamicDataSourceProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceFactory {

    public static DataSource buildDataSource(DynamicDataSourceProperties dynamicDataSourceProperties) {

        Map<String, DataSourceProperties> dataSourcePropertiesMap = dynamicDataSourceProperties.getDatasource();

        if (dataSourcePropertiesMap == null) {
            return null;
        }

        if (dataSourcePropertiesMap.size() > 1) {
            return getDynamicDataSource(dynamicDataSourceProperties);
        }
        return getDruidDataSource(dataSourcePropertiesMap.get(DataSourceHolder.getDefaultDataSourceName()));
    }

    public static DruidDataSource getDruidDataSource(DataSourceProperties dataSourceProperties) {

        return initDruidDataSource(dataSourceProperties, new DruidDataSource());
    }

    public static DynamicDataSource getDynamicDataSource(DynamicDataSourceProperties dynamicDataSourceProperties) {

        // 动态数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        Map<String, DataSourceProperties> dataSourcePropertiesMap = dynamicDataSourceProperties.getDatasource();
        Map<Object, Object> otherDataSources = new ConcurrentHashMap<>();
        dataSourcePropertiesMap.forEach((datasourceName, dataSourceProperties) -> {

            DruidXADataSource druidXADataSource = new DruidXADataSource();
            // 初始化
            initDruidDataSource(dataSourceProperties, druidXADataSource);
            AtomikosDataSourceBean atomikosDataSourceBean = convertAtomikosDataSource(datasourceName, druidXADataSource);

            if (DataSourceHolder.getDefaultDataSourceName().equals(datasourceName)) {
                // 默认数据源
                dynamicDataSource.setDefaultTargetDataSource(atomikosDataSourceBean);
            } else {
                // 其他数据源
                otherDataSources.put(datasourceName, atomikosDataSourceBean);
            }

        });

        dynamicDataSource.setTargetDataSources(otherDataSources);
        return dynamicDataSource;
    }

    public static DruidDataSource initDruidDataSource(DataSourceProperties properties, DruidDataSource druidDataSource) {

        druidDataSource.setDriverClassName(properties.getDriverClassName());
        druidDataSource.setUrl(properties.getUrl());
        druidDataSource.setUsername(properties.getUsername());
        druidDataSource.setPassword(properties.getPassword());

        druidDataSource.setInitialSize(properties.getInitialSize());
        druidDataSource.setMaxActive(properties.getMaxActive());
        druidDataSource.setMinIdle(properties.getMinIdle());
        druidDataSource.setMaxWait(properties.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        druidDataSource.setMaxEvictableIdleTimeMillis(properties.getMaxEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(properties.getValidationQuery());
        druidDataSource.setValidationQueryTimeout(properties.getValidationQueryTimeout());
        druidDataSource.setTestOnBorrow(properties.isTestOnBorrow());
        druidDataSource.setTestOnReturn(properties.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
        druidDataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        druidDataSource.setSharePreparedStatements(properties.isSharePreparedStatements());

        if (!(druidDataSource instanceof XADataSource)) {
            try {
                druidDataSource.init();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return druidDataSource;
    }

    public static AtomikosDataSourceBean convertAtomikosDataSource(String dataSourceName, XADataSource dataSource) {

        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(dataSource);
        atomikosDataSourceBean.setMinPoolSize(3);
        atomikosDataSourceBean.setMaxPoolSize(15);
        atomikosDataSourceBean.setBorrowConnectionTimeout(60);
        atomikosDataSourceBean.setUniqueResourceName(dataSourceName);
        atomikosDataSourceBean.setXaDataSourceClassName("druid-xa-datasource");

        try {
            atomikosDataSourceBean.init();
        } catch (AtomikosSQLException e) {
            e.printStackTrace();
        }

        return atomikosDataSourceBean;
    }

}
