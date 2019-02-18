package com.style.mybatis.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.style.mybatis.DynamicTransactionFactory;
import com.style.mybatis.annotation.MyBatisPlus;
import com.style.mybatis.injector.CrudSqlInjector;
import com.style.mybatis.mapper.BaseMapper;
import com.style.mybatis.plugin.dynamic.DataSourceInterceptor;
import com.style.mybatis.plugin.dynamic.DynamicDataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(MybatisPlusProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisPlusAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusAutoConfiguration.class);

    private final MybatisPlusProperties properties;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    private final ApplicationContext applicationContext;

    public MybatisPlusAutoConfiguration(MybatisPlusProperties properties,
                                        ObjectProvider<Interceptor[]> interceptorsProvider,
                                        ResourceLoader resourceLoader,
                                        ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                        ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                        ApplicationContext applicationContext) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();

        factory.setDataSource(dataSource);

        factory.setVfs(SpringBootVFS.class);

        // 添加动态数据源事务工厂
        factory.setTransactionFactory(new DynamicTransactionFactory());

        initMybatisConfiguration(factory);

        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }

        // 添加动态数据源拦截器
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            if (dataSource instanceof DynamicDataSource) {
                Interceptor[] interceptors = new Interceptor[this.interceptors.length + 1];
                for (int i = 0; i < interceptors.length; i++) {
                    if (i == 0) {
                        interceptors[i] = new DataSourceInterceptor();
                    } else {
                        interceptors[i] = this.interceptors[i - 1];
                    }
                }
                factory.setPlugins(interceptors);
            } else {
                factory.setPlugins(this.interceptors);
            }

        }

        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeEnumsPackage())) {
            factory.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }

        initGlobalConfig(factory);

        return factory.getObject();
    }

    private void initMybatisConfiguration(MybatisSqlSessionFactoryBean factory) {

        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }

        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new MybatisConfiguration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }

        factory.setConfiguration(configuration);
    }

    private void initGlobalConfig(MybatisSqlSessionFactoryBean factory) {

        GlobalConfig globalConfig = this.properties.getGlobalConfig();

        //注入填充器
        if (this.hasBeanAsApplicationContext(MetaObjectHandler.class)) {
            MetaObjectHandler metaObjectHandler = this.applicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }

        //注入主键生成器
        if (this.hasBeanAsApplicationContext(IKeyGenerator.class)) {
            IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }

        //注入sql注入器
        /*if (this.hasBeanAsApplicationContext(ISqlInjector.class)) {
            globalConfig.setSqlInjector(this.applicationContext.getBean(ISqlInjector.class));
        } else {
            globalConfig.setSqlInjector(new CrudSqlInjector());
        }*/
        globalConfig.setSqlInjector(new CrudSqlInjector());
        globalConfig.setSuperMapperClass(BaseMapper.class);
        factory.setGlobalConfig(globalConfig);
    }


    private boolean hasBeanAsApplicationContext(@Nullable Class<?> type) {
        if (this.applicationContext == null) {
            return false;
        }
        return this.applicationContext.getBeanNamesForType(ISqlInjector.class, false, false).length > 0;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
            implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            logger.debug("Searching for mappers annotated with @Mapper");

            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            try {
                if (this.resourceLoader != null) {
                    scanner.setResourceLoader(this.resourceLoader);
                }

                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
                }

                scanner.setAnnotationClass(MyBatisPlus.class);
                scanner.registerFilters();
                scanner.doScan(StringUtils.toStringArray(packages));
            } catch (IllegalStateException ex) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    /**
     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
     * creating instances of {@link MapperFactoryBean}. If
     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
     * auto-configuration is not needed. If it is _not_ used, however, then this
     * will bring in a bean registrar and automatically register components based
     * on the same component-scanning path as Spring Boot itself.
     */
    @Configuration
    @Import({AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration {

        @PostConstruct
        public void afterPropertiesSet() {
            logger.debug("No {} found.", MapperFactoryBean.class.getName());
        }
    }
}

