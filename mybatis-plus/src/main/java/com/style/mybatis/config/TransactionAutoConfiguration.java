package com.style.mybatis.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * 事务相关配置
 */
@Configuration
@EnableTransactionManagement
public class TransactionAutoConfiguration {

    public TransactionAutoConfiguration() {

    }

    /**
     * DataSource Transaction Manager
     */
    @Bean("transactionManager")
    @Primary
    @DependsOn("dataSource")
    public PlatformTransactionManager transactionManager(DataSource dataSource) throws SystemException {
        if (dataSource instanceof DynamicDataSource) {
            // JtaTransactionManager
            return new JtaTransactionManager(this.userTransaction(), this.userTransactionManager());
        } else {
            // DataSourceTransactionManager
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
            transactionManager.setDataSource(dataSource);
            return transactionManager;
        }
    }

    /**
     * 分布式事务用户接口
     */
    private UserTransaction userTransaction() throws SystemException {
        UserTransaction userTransaction = new UserTransactionImp();
        userTransaction.setTransactionTimeout(180);
        return userTransaction;
    }

    /**
     * 分布式事务管理器
     */
    private UserTransactionManager userTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }
}
