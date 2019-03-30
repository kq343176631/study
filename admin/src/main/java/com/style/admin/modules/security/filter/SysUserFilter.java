package com.style.admin.modules.security.filter;

import com.style.admin.modules.sys.utils.RedirectUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 用户权限过滤器（登录过的用户和记住我的用户可以通过）
 */
public class SysUserFilter extends org.apache.shiro.web.filter.authc.UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        RedirectUtils.redirectToDefaultPath(request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        return RedirectUtils.redirectTo403Page(request, response);
    }
}
