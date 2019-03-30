package com.style.admin.modules.security.config;

import com.style.admin.modules.security.ShiroFilterFactoryBean;
import com.style.admin.modules.security.WebSecurityManager;
import com.style.admin.modules.security.cache.ShiroJ2CacheManager;
import com.style.admin.modules.security.filter.*;
import com.style.admin.modules.security.realm.FormAuthorizingRealm;
import com.style.admin.modules.security.session.SessionDAO;
import com.style.admin.modules.security.session.SessionManager;
import com.style.utils.collect.ListUtils;
import com.style.utils.core.GlobalUtils;
import net.oschina.j2cache.CacheChannel;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.Map;

/**
 * 系统安全管理配置
 */
//@Configuration
public class ShiroAutoConfiguration {

    public ShiroAutoConfiguration() {

    }

    /**
     * Shiro认证过滤器
     */
    @Bean("shiroFilter")
    @DependsOn({"webSecurityManager", "formAuthorizingRealm", "casAuthorizingRealm"})
    public ShiroFilterFactoryBean shiroFilter(WebSecurityManager webSecurityManager,
                                              FormAuthorizingRealm formAuthorizingRealm) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(webSecurityManager);
        bean.setLoginUrl(GlobalUtils.getProperty("shiro.form.login-url"));
        bean.setSuccessUrl(GlobalUtils.getProperty("shiro.form.success-url"));
        Map<String, Filter> filters = bean.getFilters();
        filters.put("anon", anonFilter());
        filters.put("auth", authFilter(formAuthorizingRealm));
        filters.put("logout", logoutFilter());
        filters.put("perms", permsFilter());
        filters.put("roles", rolesFilter());
        filters.put("user", userFilter());
        FilterChainDefinitionMap chains = new FilterChainDefinitionMap();
        chains.setFilterChainDefinitions(GlobalUtils.getProperty("shiro.filterChainDefinitions"));
        chains.setDefaultFilterChainDefinitions(GlobalUtils.getProperty("shiro.defaultFilterChainDefinitions"));
        bean.setFilterChainDefinitionMap(chains.getObject());
        return bean;
    }

    /**
     * 定义Shiro安全管理配置
     */
    @Bean("webSecurityManager")
    @DependsOn({"formAuthorizingRealm", "sessionManager", "shiroCacheManager"})
    public WebSecurityManager webSecurityManager(
            FormAuthorizingRealm formAuthorizingRealm,
            SessionManager sessionManager, CacheManager shiroCacheManager) {
        WebSecurityManager bean = new WebSecurityManager();
        Collection<Realm> realms = ListUtils.newArrayList();
        // 第一个域：作为授权域使用。
        realms.add(formAuthorizingRealm);
        bean.setRealms(realms);
        bean.setSessionManager(sessionManager);
        bean.setCacheManager(shiroCacheManager);
        // 设置支持CAS的subjectFactory
        return bean;
    }

    /**
     * 表单安全认证实现类
     */
    @Bean("formAuthorizingRealm")
    @DependsOn({"sessionDAO"})
    public FormAuthorizingRealm formAuthorizingRealm(SessionDAO sessionDAO) {
        FormAuthorizingRealm bean = new FormAuthorizingRealm();
        bean.setCachingEnabled(false);
        bean.setSessionDAO(sessionDAO);
        return bean;
    }

    /**
     * Shiro 生命周期处理器，实现初始化和销毁回调
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * Shiro 过滤器代理配置
     */
    @Bean("defaultAdvisorAutoProxyCreator")
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    /**
     * 启用Shiro授权注解拦截方式，AOP式方法级权限检查
     */
    @Bean("authorizationAttributeSourceAdvisor")
    @DependsOn("webSecurityManager")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager webSecurityManager) {
        AuthorizationAttributeSourceAdvisor bean = new AuthorizationAttributeSourceAdvisor();
        bean.setSecurityManager(webSecurityManager);
        return bean;
    }

    /**
     * 匿名过滤器
     */
    private AnonymousFilter anonFilter() {
        return new AnonymousFilter();
    }

    /**
     * Form登录过滤器
     */
    private FormAuthenticationFilter authFilter(FormAuthorizingRealm formAuthorizingRealm) {
        FormAuthenticationFilter bean = new FormAuthenticationFilter();
        bean.setAuthorizingRealm(formAuthorizingRealm);
        return bean;
    }

    /**
     * 登出过滤器
     */
    private LogoutFilter logoutFilter() {
        return new LogoutFilter();
    }

    /**
     * 权限字符串过滤器
     */
    private PermissionsAuthorizationFilter permsFilter() {
        return new PermissionsAuthorizationFilter();
    }

    /**
     * 角色权限过滤器
     */
    private RolesAuthorizationFilter rolesFilter() {
        return new RolesAuthorizationFilter();
    }

    /**
     * 用户权限过滤器
     */
    private SysUserFilter userFilter() {
        return new SysUserFilter();
    }

    @Bean("shiroCacheManager")
    @DependsOn("cacheChannel")
    public CacheManager ShiroJ2CacheManager(CacheChannel cacheChannel) {
        return new ShiroJ2CacheManager(cacheChannel);
    }
}
