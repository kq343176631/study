package com.style.web.config;

import com.style.utils.SpringUtils;
import com.style.utils.io.PropertyUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@AutoConfigureBefore({HttpEncodingAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class})
public class WebAutoConfiguration {

    public WebAutoConfiguration() {

    }

    /**
     * springUtils
     */
    @Bean("springUtils")
    @Lazy(false)
    @Order(-2147481648)
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    /**
     * Error Page Filter
     */
    @Bean("errorPageFilter")
    @Order(-2147483647)
    public FilterRegistrationBean<ErrorPageFilter> errorPageFilter() {
        FilterRegistrationBean<ErrorPageFilter> bean = new FilterRegistrationBean<>();
        ErrorPageFilter errorPageFilter = new ErrorPageFilter();
        errorPageFilter.addErrorPages(this.registryErrorPages());
        bean.setFilter(errorPageFilter);
        bean.addUrlPatterns("/*");
        return bean;
    }

    /**
     * Encoding Filter
     */
    @Bean("characterEncodingFilter")
    @Order(1000)
    @ConditionalOnMissingBean(name = {"characterEncodingFilter"})
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
        FilterRegistrationBean<CharacterEncodingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CharacterEncodingFilter());
        bean.addInitParameter("encoding", "UTF-8");
        bean.addInitParameter("forceEncoding", "true");
        bean.addUrlPatterns("/*");
        return bean;
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

    /**
     * Property Sources Placeholder
     */
    @Bean("propertySourcesPlaceholderConfigurer")
    @Lazy(false)
    @Order(-2147483648)
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertyPlaceholder = new PropertySourcesPlaceholderConfigurer();
        propertyPlaceholder.setProperties(PropertyUtils.getInstance().getProperties());
        propertyPlaceholder.setIgnoreUnresolvablePlaceholders(true);
        return propertyPlaceholder;
    }

    /**
     * 注册系统错误页面
     */
    private ErrorPage[] registryErrorPages() {

        ErrorPage[] errorPages = new ErrorPage[6];

        // 坏的请求错误页面
        ErrorPage badRequest = new ErrorPage(HttpStatus.BAD_REQUEST, "/error/status/400.html");
        // 未经认证的错误页面
        ErrorPage unAuthorized = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/status/401.html");
        // 禁止访问错误页面
        ErrorPage forbidden = new ErrorPage(HttpStatus.FORBIDDEN, "/error/status/403.html");
        // 资源找不到错误页面
        ErrorPage notFound = new ErrorPage(HttpStatus.NOT_FOUND, "/error/status/404.html");
        // 服务器内部错误页面
        ErrorPage internalServerError = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/status/500.html");
        // 系统异常错误页面
        ErrorPage exception = new ErrorPage(Throwable.class, "/error/exception.html");

        errorPages[0] = badRequest;
        errorPages[1] = unAuthorized;
        errorPages[2] = forbidden;
        errorPages[3] = notFound;
        errorPages[4] = internalServerError;
        errorPages[5] = exception;

        return errorPages;
    }

}
