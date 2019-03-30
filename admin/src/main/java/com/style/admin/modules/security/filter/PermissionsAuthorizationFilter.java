package com.style.admin.modules.security.filter;

import com.style.admin.modules.sys.utils.RedirectUtils;
import com.style.common.web.http.GetHttpServletRequestWrapper;
import com.style.common.web.servlet.ServletUtils;
import com.style.utils.core.GlobalUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 字符串权限过滤器
 */
public class PermissionsAuthorizationFilter extends org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        RedirectUtils.redirectToDefaultPath(request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        return RedirectUtils.redirectTo403Page(request, response);
    }
}
