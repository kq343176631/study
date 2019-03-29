package com.style.admin.modules.security.filter;

import com.style.admin.modules.security.authc.CasToken;
import com.style.admin.modules.security.realm.BaseAuthorizingRealm;
import com.style.utils.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 单点登录过滤类
 *
 * @author ThinkGem
 * @version 2017-03-22
 */
@SuppressWarnings("deprecation")
public class CasAuthenticationFilter extends org.apache.shiro.cas.CasFilter {

    private static final String TICKET_PARAMETER = "ticket";

    // 安全认证类
    private BaseAuthorizingRealm authorizingRealm;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ticket = httpRequest.getParameter(TICKET_PARAMETER);
        return new CasToken(ticket);
    }

    /**
     * 登录成功调用事件
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

        // 登录成功后初始化授权信息并处理登录后的操作
        //authorizingRealm.onLoginSuccess(subject.getPrincipals());

        String url = request.getParameter("__url");
        if (StringUtils.isNotBlank(url)) {
            WebUtils.issueRedirect(request, response, url, null, true);
        } else {
            WebUtils.issueRedirect(request, response, getSuccessUrl(), null, true);
        }
        return false;
    }

    /**
     * 登录失败调用事件
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                String url = request.getParameter("__url");
                if (StringUtils.isNotBlank(url)) {
                    WebUtils.issueRedirect(request, response, url, null, true);
                } else {
                    WebUtils.issueRedirect(request, response, getSuccessUrl(), null, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            try {
                if (ae != null && StringUtils.startsWith(ae.getMessage(), "msg:")) {
                    request.setAttribute("exception", ae);
                    request.getRequestDispatcher("/error/403").forward(request, response);
                } else {
                    WebUtils.issueRedirect(request, response, getLoginUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void setAuthorizingRealm(BaseAuthorizingRealm authorizingRealm) {
        this.authorizingRealm = authorizingRealm;
    }

}
