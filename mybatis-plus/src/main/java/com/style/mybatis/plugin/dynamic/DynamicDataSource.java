package com.style.mybatis.plugin.dynamic;

import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.style.common.GlobalUtils;
import com.style.mybatis.DatasourceException;
import com.style.mybatis.config.DataSourceAutoConfiguration;
import com.style.common.lang.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource implements DisposableBean {

    private final String defaultDataSourceName = GlobalUtils.getDefaultDataSourceName();

    private Map<Object, Object> targetDataSources = null;

    private Object defaultTargetDataSource = null;

    /**
     * 从本地线程中获取数据源
     *
     * @return curDataSourceName
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getCurDataSourceName();
    }

    /**
     * 添加数据源
     *
     * @param dataSourceName dataSourceName
     * @param dataSource     dataSource
     */
    public void addTargetDataSource(String dataSourceName, DataSource dataSource) {
        this.targetDataSources.put(dataSourceName, dataSource);
        super.afterPropertiesSet();
    }

    /**
     * 移除数据源
     *
     * @param dataSourceName dataSourceName
     */
    public void removeTargetDataSource(String dataSourceName) {
        this.targetDataSources.remove(dataSourceName);
        super.afterPropertiesSet();
    }

    /**
     * 重置
     */
    @Override
    public void destroy() {
        ContextSelector contextSelector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
        contextSelector.detachLoggerContext(contextSelector.getLoggerContext().getName()).reset();
    }

    /**
     * 设置目标数据源
     *
     * @param targetDataSources targetDataSources
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(this.targetDataSources = targetDataSources);
    }

    /**
     * 设置默认数据源
     *
     * @param defaultTargetDataSource defaultTargetDataSource
     */
    @Override
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        super.setDefaultTargetDataSource(this.defaultTargetDataSource = defaultTargetDataSource);
    }

    /**
     * 获取目标数据源
     *
     * @param dataSourceName dataSourceName
     * @return DataSource
     */
    public DataSource getTargetDataSource(String dataSourceName) {
        if (StringUtils.isNotBlank(dataSourceName) && !this.defaultDataSourceName.equals(dataSourceName)) {
            Object object = this.targetDataSources.get(dataSourceName);
            if (object != null) {
                return (DataSource) object;
            }
        }
        return (DataSource) this.defaultTargetDataSource;
    }

    /**
     * 初始化数据源
     */
    @Override
    public void afterPropertiesSet() {
        String[] targetDataSourceNames = GlobalUtils.getTargetDataSourceNames();
        if (targetDataSourceNames != null && targetDataSourceNames.length > 1) {
            for (int i = 1; i < targetDataSourceNames.length; i++) {
                String dataSourceName = targetDataSourceNames[i];
                try {
                    DataSource targetDataSource = DataSourceAutoConfiguration.initDataSource(dataSourceName,
                            new DruidXADataSource());
                    targetDataSource = DataSourceAutoConfiguration.convertAtomikosDataSource(dataSourceName, targetDataSource);
                    this.targetDataSources.put(dataSourceName, targetDataSource);
                } catch (SQLException e) {
                    logger.error("加载多数据源" + dataSourceName + "失败。。。");
                } catch (DatasourceException e) {
                    e.printStackTrace();
                }

            }
        }
        // 开始解析数据源
        super.afterPropertiesSet();
    }

}
