package com.style.datasource;

import com.style.datasource.plugin.DynamicDataSource;
import com.style.datasource.plugin.DynamicTransaction;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * 分布式事务工厂
 */
public class MybatisTransactionFactory extends SpringManagedTransactionFactory {
    /**
     * 创建事务
     *
     * @param dataSource dataSource
     * @param level      level
     * @param autoCommit autoCommit
     * @return 事务对象
     */
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        if (dataSource instanceof DynamicDataSource) {
            // 开启分布式事务
            return new DynamicTransaction(dataSource);
        }
        return new SpringManagedTransaction(dataSource);
    }
}
