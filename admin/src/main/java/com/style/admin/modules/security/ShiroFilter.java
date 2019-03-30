package com.style.admin.modules.security;

import com.style.common.web.servlet.ServletUtils;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 重写过滤方法：优化静态文件的处理。
 */
public class ShiroFilter extends AbstractShiroFilter {

    /**
     * 构造方法
     */
    protected ShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver filterChainResolver) {
        super();
        if (webSecurityManager == null) {
            throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
        }
        this.setSecurityManager(webSecurityManager);
        if (filterChainResolver != null) {
            this.setFilterChainResolver(filterChainResolver);
        }
    }

    /**
     * 重写过滤方法：优化静态文件的处理。
     */
    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (ServletUtils.isStaticFile(((HttpServletRequest) request).getRequestURI())) {
            // 静态文件直接放行，提高性能。
            chain.doFilter(request, response);
        } else {
            // 不是静态文件，则执行内部过滤。
            super.doFilterInternal(request, response, chain);
        }
    }
}
