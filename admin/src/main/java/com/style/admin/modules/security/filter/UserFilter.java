package com.style.admin.modules.security.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 用户权限过滤器（登录过的用户和记住我的用户可以通过）
 *
 * @author ThinkGem
 * @version 2017-03-22
 */
public class UserFilter extends org.apache.shiro.web.filter.authc.UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        PermissionsAuthorizationFilter.redirectToDefaultPath(request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        return PermissionsAuthorizationFilter.redirectTo403Page(request, response);
    }
}
