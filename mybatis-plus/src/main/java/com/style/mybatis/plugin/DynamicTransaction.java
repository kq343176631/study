package com.style.mybatis.plugin;

import com.style.mybatis.plugin.dynamic.DynamicDataSourceHolder;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import com.style.mybatis.utils.StringUtils;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式事务
 */
public class DynamicTransaction implements Transaction {
    private final Map<String, SpringManagedTransaction> transactions;
    private final DynamicDataSource dynamicDataSource;

    public DynamicTransaction(DataSource dataSource) {
        this.dynamicDataSource = (DynamicDataSource) dataSource;
        this.transactions = new ConcurrentHashMap<>();
    }

    @Override
    public Connection getConnection() throws SQLException {
        String dataSourceName = this.getCurrentDataSourceName();
        SpringManagedTransaction transaction;
        if ((transaction = this.transactions.get(dataSourceName)) == null) {
            DataSource dataSource = this.dynamicDataSource.getTargetDataSource(dataSourceName);
            transaction = new SpringManagedTransaction(dataSource);
            this.transactions.put(dataSourceName, transaction);
        }
        return transaction.getConnection();
    }

    @Override
    public void commit() throws SQLException {
        Iterator iterator;
        for (Iterator tmp = iterator = this.transactions.entrySet().iterator(); tmp.hasNext(); tmp = iterator) {
            ((SpringManagedTransaction) ((Map.Entry) iterator.next()).getValue()).commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        Iterator iterator;
        for (Iterator tmp = iterator = this.transactions.entrySet().iterator(); tmp.hasNext(); tmp = iterator) {
            ((SpringManagedTransaction) ((Map.Entry) iterator.next()).getValue()).rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        Iterator iterator;
        for (Iterator tmp = iterator = this.transactions.entrySet().iterator(); tmp.hasNext(); tmp = iterator) {
            ((SpringManagedTransaction) ((Map.Entry) iterator.next()).getValue()).close();
        }

        this.transactions.clear();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        SpringManagedTransaction transaction;
        return (transaction = this.transactions.get(this.getCurrentDataSourceName())) != null ? transaction.getTimeout() : null;
    }

    /**
     * 获取当前线程的数据源名称
     *
     * @return dataSourceName
     */
    public String getCurrentDataSourceName() {
        String dataSourceName = DynamicDataSourceHolder.getCurDataSourceName();
        return StringUtils.isNotBlank(dataSourceName) ? dataSourceName : DynamicDataSourceHolder.getDefaultDataSourceName();
    }
}
