package com.style.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosSQLException;
import com.style.common.lang.MapUtils;
import com.style.mybatis.DataSourceProperties;
import com.style.mybatis.DynamicDataSourceProperties;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 数据源配置
 */
@Configuration
@EnableConfigurationProperties
public class DataSourceAutoConfiguration {

    public static String XA_DATA_SOURCE_CLASS_NAME = "com.alibaba.druid.pool.xa.DruidXADataSource";

    public DataSourceAutoConfiguration() {

    }

    /**
     * 默认数据源
     */
    @Bean("defaultDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 其他数据源
     */
    @Bean("dynamicDataSourceProperties")
    @ConfigurationProperties(prefix = "dynamic")
    public DynamicDataSourceProperties dynamicDataSourceProperties() {
        return new DynamicDataSourceProperties();
    }

    /**
     * 数据源
     */
    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DataSourceProperties defaultDataSourceProperties, DynamicDataSourceProperties dynamicDataSourceProperties) {

        if (dynamicDataSourceProperties != null && dynamicDataSourceProperties.getDatasource().size() > 0) {
            // 动态数据源
            DynamicDataSource dynamicDataSource = new DynamicDataSource();
            // 默认数据源
            dynamicDataSource.setDefaultTargetDataSource(convertAtomikosDataSource("default",
                    (DruidXADataSource) initDataSource(defaultDataSourceProperties, new DruidXADataSource())));
            // 其他数据源
            Map<String, DataSourceProperties> dataSourcePropertiesMap = dynamicDataSourceProperties.getDatasource();
            Map<Object, Object> targetDataSources = MapUtils.newConcurrentMap();
            dataSourcePropertiesMap.forEach((datasourceName, dataSourceProperties) -> {

                DataSource dataSource = initDataSource(dataSourceProperties, new DruidXADataSource());

                targetDataSources.put(datasourceName, convertAtomikosDataSource(datasourceName, (DruidXADataSource) dataSource));
            });
            dynamicDataSource.setTargetDataSources(targetDataSources);
            return dynamicDataSource;
        }
        return initDataSource(defaultDataSourceProperties, new DruidDataSource());
    }

    /**
     * @param properties      properties
     * @param druidDataSource dataSource
     * @return dataSource
     */
    public static DataSource initDataSource(DataSourceProperties properties, DruidDataSource druidDataSource) {

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

    /**
     * 将XA数据源转换为AtomikosDataSource
     *
     * @param dataSourceName dataSourceName
     * @param dataSource     dataSource
     * @return AtomikosDataSourceBean
     */
    public static AtomikosDataSourceBean convertAtomikosDataSource(String dataSourceName, XADataSource dataSource) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(dataSource);
        atomikosDataSourceBean.setMinPoolSize(3);
        atomikosDataSourceBean.setMaxPoolSize(15);
        atomikosDataSourceBean.setBorrowConnectionTimeout(60);
        atomikosDataSourceBean.setUniqueResourceName(dataSourceName);
        atomikosDataSourceBean.setXaDataSourceClassName(XA_DATA_SOURCE_CLASS_NAME);
        try {
            atomikosDataSourceBean.init();
        } catch (AtomikosSQLException e) {
            e.printStackTrace();
        }
        return atomikosDataSourceBean;
    }

}
