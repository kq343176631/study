package com.style.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosSQLException;
import com.style.mybatis.plugin.dynamic.ConnectionPool;
import com.style.mybatis.DatasourceException;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import com.style.common.lang.MapUtils;
import com.style.common.system.GlobalUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.SQLException;

/**
 * 数据源配置
 */
@Configuration
@AutoConfigureBefore({org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class DataSourceAutoConfiguration {

    public static String DATA_SOURCE_KEY_PREFIX = "target-datasource";

    public static String DATA_SOURCE_NAME_KEY_PREFIX = "target-datasource.datasource-name";

    public static String XA_DATA_SOURCE_CLASS_NAME = "com.alibaba.druid.pool.xa.DruidXADataSource";


    public DataSourceAutoConfiguration() {

    }

    /**
     * 数据源
     *
     * @return datasource
     */
    @Bean("dataSource")
    @Primary
    public DataSource dataSource() throws SQLException, DatasourceException {
        DataSource dataSource = determineDataSource();
        String defaultDataSourceName = GlobalUtils.getDefaultDataSourceName();
        // 动态数据源
        if (dataSource instanceof DynamicDataSource) {
            DynamicDataSource dynamicDataSource = (DynamicDataSource) dataSource;
            dynamicDataSource.setDefaultTargetDataSource(convertAtomikosDataSource(defaultDataSourceName,
                    initDataSource(defaultDataSourceName, new DruidXADataSource())));
            dynamicDataSource.setTargetDataSources(MapUtils.newConcurrentMap());
            return dynamicDataSource;
        }
        return initDataSource(defaultDataSourceName, dataSource);
    }

    /**
     * 选择数据源
     *
     * @return datasource
     */
    private DataSource determineDataSource() {
        if (GlobalUtils.isEnableDynamicDataSource()) {
            return new DynamicDataSource();
        }
        return new DruidDataSource();
    }

    /**
     * @param dataSourceName dataSourceName
     * @param dataSource     dataSource
     * @return dataSource
     * @throws SQLException SQLException
     */
    public static DataSource initDataSource(String dataSourceName, DataSource dataSource) throws SQLException {
        ConnectionPool entity = getDataSourceConfig(dataSourceName);
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            druidDataSource.setDriverClassName(entity.getDriverClassName());
            druidDataSource.setUrl(entity.getUrl());
            druidDataSource.setUsername(entity.getUsername());
            druidDataSource.setPassword(entity.getPassword());
            druidDataSource.setTestOnBorrow(entity.getTestOnBorrow());
            druidDataSource.setTestOnReturn(entity.getTestOnReturn());
            druidDataSource.setInitialSize(entity.getPoolInitSize());
            druidDataSource.setMinIdle(entity.getPoolMinSize());
            druidDataSource.setMaxActive(entity.getPoolMaxSize());
            druidDataSource.setMaxWait(entity.getMaxWait());
            druidDataSource.setTimeBetweenEvictionRunsMillis(entity.getTimeBetweenEvictionRunsMillis());
            druidDataSource.setMinEvictableIdleTimeMillis(entity.getMinEvictableIdleTimeMillis());
            druidDataSource.setRemoveAbandoned(entity.getRemoveAbandoned());
            druidDataSource.setRemoveAbandonedTimeout(entity.getRemoveAbandonedTimeout());
            if (!(druidDataSource instanceof XADataSource)) {
                druidDataSource.init();
            }
        }
        return dataSource;
    }

    /**
     * 将XA数据源转换为AtomikosDataSource
     *
     * @param dataSourceName dataSourceName
     * @param dataSource     dataSource
     * @return AtomikosDataSourceBean
     * @throws AtomikosSQLException AtomikosSQLException
     */
    public static AtomikosDataSourceBean convertAtomikosDataSource(String dataSourceName, DataSource dataSource) throws AtomikosSQLException, DatasourceException {
        AtomikosDataSourceBean atomikosDataSourceBean;
        if (dataSource instanceof XADataSource) {
            atomikosDataSourceBean = new AtomikosDataSourceBean();
            atomikosDataSourceBean.setXaDataSource((XADataSource) dataSource);
            atomikosDataSourceBean.setMinPoolSize(3);
            atomikosDataSourceBean.setMaxPoolSize(15);
            atomikosDataSourceBean.setBorrowConnectionTimeout(60);
            atomikosDataSourceBean.setUniqueResourceName(dataSourceName);
            atomikosDataSourceBean.setXaDataSourceClassName(XA_DATA_SOURCE_CLASS_NAME);
            atomikosDataSourceBean.init();
        } else {
            throw new DatasourceException("当前数据源不支持XA协议");
        }
        return atomikosDataSourceBean;
    }

    /**
     * 获取数据源配置信息
     *
     * @param dataSourceName dataSourceName
     * @return dataSourceEntity
     */
    public static ConnectionPool getDataSourceConfig(String dataSourceName) {
        String dataSourceKey = DATA_SOURCE_KEY_PREFIX;
        return new ConnectionPool(dataSourceName,
                GlobalUtils.getProperty(dataSourceKey + "." + dataSourceName + "." + "driver"),
                GlobalUtils.getProperty(dataSourceKey + "." + dataSourceName + "." + "url"),
                GlobalUtils.getProperty(dataSourceKey + "." + dataSourceName + "." + "username"),
                GlobalUtils.getProperty(dataSourceKey + "." + dataSourceName + "." + "password")
        );
    }
}
