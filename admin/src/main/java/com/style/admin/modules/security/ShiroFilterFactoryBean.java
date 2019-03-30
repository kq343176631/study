package com.style.admin.modules.security;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;

public class ShiroFilterFactoryBean extends org.apache.shiro.spring.web.ShiroFilterFactoryBean {

    // 过滤器实例
    private AbstractShiroFilter instance;

    public ShiroFilterFactoryBean() {
    }

    /**
     * 创建过滤器实例
     */
    @Override
    protected AbstractShiroFilter createInstance(){

        SecurityManager securityManager = this.getSecurityManager();
        // 先决条件验证
        if (securityManager == null) {
            throw new BeanInitializationException("SecurityManager property must be set.");
        }
        if (!(securityManager instanceof WebSecurityManager)) {
            throw new BeanInitializationException("The security manager does not implement the WebSecurityManager interface.");
        }
        // 过滤链解析器
        FilterChainManager chainManager = this.createFilterChainManager();
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(chainManager);

        return new ShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }

    /**
     * 获取滤器实例
     */
    public AbstractShiroFilter getInstance() throws Exception {
        if (this.instance == null) {
            this.instance = this.createInstance();
        }
        return this.instance;
    }

    @Override
    public Class<?> getObjectType() {
        return ShiroFilterFactoryBean.class;
    }

    @Override
    public Object getObject() {
        return this;
    }

}
