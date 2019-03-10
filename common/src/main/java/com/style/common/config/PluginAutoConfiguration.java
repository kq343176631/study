package com.style.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 插件的代理类生成顺序：PaginationInterceptor->DataScopeInterceptor
 * 插件拦截方法执行顺序：DataScopeInterceptor->PaginationInterceptor
 */
@Configuration
public class PluginAutoConfiguration {

    /**
     * 数据范围
     */
    /*@Bean
    @Order(1)
    public DataScopeInterceptor dataScopeInterceptor() {
        return new DataScopeInterceptor();
    }*/

    /**
     * 配置分页
     */
    @Bean
    @Order(0)
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
