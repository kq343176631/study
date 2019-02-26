package com.style.common.config;

import com.style.common.web.filter.XssFilter;
import com.style.utils.core.SpringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.DispatcherType;

@Configuration
public class WebAutoConfiguration {

    public WebAutoConfiguration() {

    }

    /**
     * SpringUtils
     */
    @Bean("springUtils")
    @Lazy(false)
    @Order(-2147481648)
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    /**
     * Request Context Listener
     */
    @Bean("requestContextListener")
    @Order(1000)
    @ConditionalOnMissingBean(name = {"requestContextListener"})
    public ServletListenerRegistrationBean<RequestContextListener> requestContextListener() {
        ServletListenerRegistrationBean<RequestContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new RequestContextListener());
        return bean;
    }

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

}
