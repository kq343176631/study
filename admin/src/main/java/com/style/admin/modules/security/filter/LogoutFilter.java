package com.style.admin.modules.security.filter;

import com.style.common.constant.Constants;
import com.style.common.exception.ValidateException;
import com.style.common.web.servlet.ServletUtils;
import com.style.utils.core.GlobalUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登出过滤器
 *
 * @author ThinkGem
 * @version 2017-03-22
 */
public class LogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {

    private static final Logger log = LoggerFactory.getLogger(LogoutFilter.class);

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) {
        try {
            Subject subject = getSubject(request, response);
            if (subject == null) {
                throw new ValidateException("");
            }
            String redirectUrl = getRedirectUrl(request, response, subject);
            try {
                // 记录用户退出日志
                //LogUtils.saveLog(ServletUtils.getRequest(), "系统退出", SysConstant.LOGOUT_SYSTEM, null);
                // 退出登录
                subject.logout();
            } catch (SessionException ise) {
                log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
            }

            // 如果是Ajax请求，返回Json字符串。
            if (ServletUtils.isAjaxRequest((HttpServletRequest) request)) {
                ServletUtils.renderString((HttpServletResponse) response,
                        Constants.TRUE, GlobalUtils.getText("sys.logout.success"));
                return false;
            }

            issueRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.debug("Encountered red session exception during logout.  This can generally safely be ignored.", e);
        }
        return false;
    }

    /**
     * 登出跳转URL
     */
    @Override
    protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
		/*String url = GlobalUtils.getProperty("shiro.logoutUrl");
		// 如果配置了登出之后跳转的url，并且url不能为 ${adminPath}/logout 否则会造成死循环。
		if (StringUtils.isNoneBlank(url) && !url.equals((GlobalUtils.getAdminPath()+"/logout"))){
			return url;
		}*/
        return super.getRedirectUrl(request, response, subject);
    }
}
