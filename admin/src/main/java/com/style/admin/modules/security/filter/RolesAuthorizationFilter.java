package com.style.admin.modules.security.filter;

import com.style.admin.modules.sys.utils.RedirectUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 角色权限过滤器
 */
public class RolesAuthorizationFilter extends org.apache.shiro.web.filter.authz.RolesAuthorizationFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        RedirectUtils.redirectToDefaultPath(request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        return RedirectUtils.redirectTo403Page(request, response);
    }
}
