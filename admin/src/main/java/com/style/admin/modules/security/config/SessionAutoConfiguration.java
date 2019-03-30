package com.style.admin.modules.security.config;

import com.style.admin.modules.security.session.J2SessionDAO;
import com.style.admin.modules.security.session.SessionDAO;
import com.style.admin.modules.security.session.SessionManager;
import com.style.utils.core.GlobalUtils;
import com.style.utils.id.IdGenUtils;
import com.style.utils.lang.ObjectUtils;
import net.oschina.j2cache.CacheChannel;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 自定义会话管理配置
 */
@Configuration
public class SessionAutoConfiguration {

    public SessionAutoConfiguration() {

    }

    /**
     * 会话管理器
     */
    @Bean("sessionManager")
    @DependsOn({"sessionDAO", "sessionIdCookie"})
    public SessionManager sessionManager(SessionDAO sessionDAO, SimpleCookie sessionIdCookie) {
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        //会话超时时间，单位：毫秒
        sessionManager.setGlobalSessionTimeout(ObjectUtils.toLong(GlobalUtils.getProperty("session.session-timeout")));
        //定时清理失效会话, 清理用户直接关闭浏览器造成的孤立会话
        sessionManager.setSessionValidationInterval(ObjectUtils.toLong(GlobalUtils.getProperty("session.session-timeout-clean")));
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    /**
     * 自定义会话存储
     */
    @Bean("sessionDAO")
    @DependsOn("cacheChannel")
    public SessionDAO sessionDAORedis(CacheChannel cacheChannel) {
        J2SessionDAO j2SessionDAO = new J2SessionDAO(cacheChannel);
        j2SessionDAO.setSessionIdGenerator(session -> IdGenUtils.uuid());
        j2SessionDAO.setRegion("sys_session");
        return j2SessionDAO;
    }

    @Bean("sessionIdCookie")
    public SimpleCookie sessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        // 设置cookieId
        simpleCookie.setName(GlobalUtils.getProperty("session.session-id-cookie-name"));
        return simpleCookie;
    }
}
