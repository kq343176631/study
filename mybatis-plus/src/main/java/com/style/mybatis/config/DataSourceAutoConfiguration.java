package com.style.mybatis.config;

import com.style.mybatis.fatory.DataSourceFactory;
import com.style.mybatis.plugin.dynamic.DataSourceHolder;
import com.style.mybatis.properties.DataSourceProperties;
import com.style.mybatis.properties.DynamicDataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
@Configuration
@EnableConfigurationProperties
public class DataSourceAutoConfiguration {

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
    @DependsOn("defaultDataSourceProperties")
    @ConfigurationProperties(prefix = "dynamic")
    public DynamicDataSourceProperties dynamicDataSourceProperties(DataSourceProperties properties) {
        DynamicDataSourceProperties dynamicDataSourceProperties = new DynamicDataSourceProperties();
        // 添加默认数据源
        dynamicDataSourceProperties.getDatasource().put(DataSourceHolder.getDefaultDataSourceName(), properties);
        return dynamicDataSourceProperties;
    }

    /**
     * 数据源
     */
    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DynamicDataSourceProperties dynamicDataSourceProperties) {
        return DataSourceFactory.buildDataSource(dynamicDataSourceProperties);
    }

}
