package com.style.admin.modules.sys.utils;

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
 * 页面重定向工具类
 */
public class RedirectUtils {

    /**
     * 无访问权限时，跳转到403页面
     */
    public static boolean redirectTo403Page(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            redirectToDefaultPath(request, response);
        } else {
            try {
                // 如果访问的是未授权页面，则直接转到403页面（2016-11-3）
                request.getRequestDispatcher("/error/403").forward(request, response);
            } catch (ServletException e) {
                throw new UnauthorizedException(e);
            }
        }
        return false;
    }

    /**
     * 跳转登录页时，跳转到默认首页
     */
    public static void redirectToDefaultPath(ServletRequest request, ServletResponse response) throws IOException {
        // AJAX不支持Redirect改用Forward
        String loginUrl = GlobalUtils.getProperty("shiro.defaultPath");
        if (ServletUtils.isAjaxRequest((HttpServletRequest) request)) {
            try {
                request.getRequestDispatcher(loginUrl).forward(new GetHttpServletRequestWrapper(request), response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        } else {
            WebUtils.issueRedirect(request, response, loginUrl);
        }
    }

}
