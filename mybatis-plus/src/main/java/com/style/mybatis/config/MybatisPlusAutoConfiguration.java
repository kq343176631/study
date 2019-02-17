package com.style.mybatis.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.style.common.GlobalUtils;
import com.style.common.io.ResourceUtils;
import com.style.common.lang.ListUtils;
import com.style.common.lang.ObjectUtils;
import com.style.common.lang.StringUtils;
import com.style.mybatis.DynamicTransactionFactory;
import com.style.mybatis.annotation.MyBatisPlus;
import com.style.mybatis.dao.BaseDao;
import com.style.mybatis.injector.CommonFieldHandler;
import com.style.mybatis.injector.CrudSqlInjector;
import com.style.mybatis.plugin.dynamic.DataSourceInterceptor;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@AutoConfigureAfter({TransactionAutoConfiguration.class})
public class MybatisPlusAutoConfiguration {

    /**
     * sqlSessionFactoryBean
     */
    @Bean("sqlSessionFactory")
    @DependsOn("dataSource")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {

        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();

        // 设置数据源
        factory.setDataSource(dataSource);
        // VFS
        factory.setVfs(SpringBootVFS.class);
        // 设置事务工厂
        factory.setTransactionFactory(new DynamicTransactionFactory());

        factory.setMapperLocations(ResourceUtils.getResources(
                GlobalUtils.getProperty("mybatis-plus.mapper-locations")
        ));
        factory.setTypeAliasesPackage(GlobalUtils.getProperty("mybatis-plus.typeAliasesPackage"));

        // 根据数据源类型设置拦截器
        List<Interceptor> plugins = ListUtils.newArrayList();
        plugins.add(new PaginationInterceptor());
        if (dataSource instanceof DynamicDataSource) {
            plugins.add(new DataSourceInterceptor());
        }
        factory.setPlugins(ListUtils.toArray(plugins, new Interceptor[plugins.size()]));

        // 全局配置
        factory.setGlobalConfig(this.getGlobalConfig());
        // 默认配置
        factory.setConfiguration(this.getMybatisConfiguration());

        return factory.getObject();
    }

    /**
     * 配置Mapper扫描器
     * 该扫描器实现了BeanDefinitionRegistryPostProcessor
     * 需要在加static修饰符
     */
    @Bean("mapperScannerConfigurer")
    public static MapperScannerConfigurer mapperScannerConfigurer() {

        MapperScannerConfigurer scanner = new MapperScannerConfigurer();
        // 设置DAO注解
        scanner.setAnnotationClass(MyBatisPlus.class);
        // 设置包扫描路径
        scanner.setBasePackage(GlobalUtils.getProperty("mybatis-plus.mapper-scan-path"));

        return scanner;
    }

    private GlobalConfig getGlobalConfig() {

        GlobalConfig globalConfig = new GlobalConfig();

        globalConfig.setDbConfig(new GlobalConfig.DbConfig()
                .setIdType(IdType.ID_WORKER_STR)
                .setFieldStrategy(FieldStrategy.NOT_NULL)
                .setTableUnderline(true)
        );
        globalConfig.setBanner(false);
        globalConfig.setSqlParserCache(ObjectUtils.toBoolean(
                GlobalUtils.getProperty("mybatis-plus.global-config.sql-parser-cache")
        ));
        globalConfig.setSuperMapperClass(BaseDao.class);

        //注入填充器
        globalConfig.setMetaObjectHandler(new CommonFieldHandler());
        //注入sql注入器
        globalConfig.setSqlInjector(new CrudSqlInjector());
        //注入主键生成器
        //globalConfig.getDbConfig().setKeyGenerator(keyGenerator);

        return globalConfig;
    }

    private MybatisConfiguration getMybatisConfiguration() {

        MybatisConfiguration configuration = new MybatisConfiguration();

        configuration.setCacheEnabled(ObjectUtils.toBoolean(
                GlobalUtils.getProperty("mybatis-plus.configuration.cache-enabled")
        ));
        configuration.setMapUnderscoreToCamelCase(ObjectUtils.toBoolean(
                GlobalUtils.getProperty("mybatis-plus.configuration.map-underscore-to-camel-case")
        ));

        configuration.setCallSettersOnNulls(ObjectUtils.toBoolean(
                GlobalUtils.getProperty("mybatis-plus.configuration.call-setters-on-nulls")
        ));
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setLazyLoadingEnabled(true);

        String executorType = GlobalUtils.getProperty("mybatis-plus.configuration.executor-type");
        if (StringUtils.isNotBlank(executorType)) {
            if ("simple".equals(executorType.toLowerCase())) {
                configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
            } else if ("reuse".equals(executorType.toLowerCase())) {
                configuration.setDefaultExecutorType(ExecutorType.REUSE);
            } else if ("batch".equals(executorType.toLowerCase())) {
                configuration.setDefaultExecutorType(ExecutorType.BATCH);
            }
        } else {
            configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
        }

        return configuration;
    }

}
