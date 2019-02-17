package com.style.web.config;

import com.style.common.SpringUtils;
import com.style.common.io.PropertyUtils;
import com.style.common.lang.ListUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

@Configuration
@AutoConfigureBefore({HttpEncodingAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class})
public class WebCoreAutoConfiguration {

    public WebCoreAutoConfiguration() {

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
    /*@Bean("errorPageFilter")
    @Order(-2147483647)
    public FilterRegistrationBean<ErrorPageFilter> errorPageFilter() {
        FilterRegistrationBean<ErrorPageFilter> bean = new FilterRegistrationBean<>();
        ErrorPageFilter errorPageFilter = new ErrorPageFilter();
        errorPageFilter.addErrorPages(this.registryErrorPages());
        bean.setFilter(errorPageFilter);
        bean.addUrlPatterns("/*");
        return bean;
    }*/

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
     * Apache Security Filter
     */
    /*@Bean("shiroFilterProxy")
    @Order(2000)
    @ConditionalOnMissingBean(name = {"shiroFilterProxy"})
    @DependsOn({"shiroFilter"})
    public FilterRegistrationBean<Filter> shiroFilterProxy(ShiroFilterFactoryBean shiroFilter) throws Exception {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(shiroFilter.getInstance());
        bean.addUrlPatterns("/*");
        return bean;
    }*/

    /*
    @Bean("druidFilter")
    @Order(3000)
    @ConditionalOnProperty(
            name = "druid.stat.enabled",
            havingValue = "true", matchIfMissing = true
    )
    public FilterRegistrationBean<WebStatFilter> druidFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        bean.addInitParameter("exclusions", "*.css,*.js,*.png,"
                + "*.jpg,*.gif,*.jpeg,*.bmp,*.ico,*.swf,*.psd,*.htc,*.htm,*.html,"
                + "*.crx,*.xpi,*.exe,*.ipa,*.apk,*.otf,*.eot,*.svg,*.ttf,*.woff,"
                + "/druid/*");
        bean.addUrlPatterns("/*");
        return bean;
    }*/

     /*
    @Bean
    @Order(2000)
    @ConditionalOnProperty(name = "ehcache.pageCaching.enabled", havingValue = "true")
    public FilterRegistrationBean pageCachingFilter(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        PageCachingFilter pageCachingFilter = new PageCachingFilter();
        pageCachingFilter.setCacheManager(ehCacheManagerFactoryBean.getObject());
        bean.setFilter(pageCachingFilter);
        bean.addInitParameter("cacheName", "pageCachingFilter");
        bean.addUrlPatterns(StringUtils.split(GlobalUtils.getProperty(
                "ehcache.pageCaching.urlPatterns"), ","));
        return bean;
    }*/


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

    /*
    @Bean("druidServlet")
    @ConditionalOnProperty(
            name = "druid.stat.enabled",
            havingValue = "true", matchIfMissing = true
    )
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>();
        bean.setServlet(new StatViewServlet());
        bean.addUrlMappings("/druid/*");
        return bean;
    }*/

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
    private List<ErrorPage> registryErrorPages() {

        List<ErrorPage> errorPages = ListUtils.newArrayList();
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

        errorPages.add(badRequest);
        errorPages.add(unAuthorized);
        errorPages.add(forbidden);
        errorPages.add(notFound);
        errorPages.add(internalServerError);
        errorPages.add(exception);

        return errorPages;
    }

}
