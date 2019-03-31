package com.style.admin.modules.security.session;

import com.style.common.web.servlet.ServletUtils;
import com.style.utils.lang.StringUtils;
import com.style.utils.network.IpUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * 自定义WEB会话管理类
 */
public class WebSessionManager extends DefaultWebSessionManager {

    public WebSessionManager() {

    }

    /**
     * 创建会话，此时并没有保存会话。
     */
    @Override
    protected Session newSessionInstance(SessionContext context) {
        Session session;
        if ((session = super.newSessionInstance(context)) instanceof SimpleSession) {
            ((SimpleSession) session).setHost(IpUtils.getIpAddr(ServletUtils.getHttpServletRequest()));
        }
        session.setTimeout(getGlobalSessionTimeout());
        return session;
    }

    /**
     * 获取会话的属性
     */
    @Override
    public Object getAttribute(SessionKey sessionKey, Object attributeKey) {
        try {
            return super.getAttribute(sessionKey, attributeKey);
        } catch (InvalidSessionException var4) {
            // 获取不到SESSION不抛出异常
            return null;
        }
    }

    /**
     * 更新会话的最后访问时间
     * 如果是Web应用，每次进入ShiroFilter都会自动调用session.touch()来更新最后访问时间。
     *
     * @param key SessionKey
     */
    @Override
    public void touch(SessionKey key) {
        try {
            Session session;
            if ((session = this.doGetSession(key)) != null) {
                HttpServletRequest request;
                if ((request = ServletUtils.getHttpServletRequest()) != null) {
                    if (ServletUtils.isStaticFile(request.getRequestURI())) {
                        return;
                    }
                    String isUpdateTime = request.getParameter("not_update_last_visit_time");
                    if ("true".equals(isUpdateTime) || "1".equals(isUpdateTime)) {
                        return;
                    }
                }
                // 更新会话的最后访问时间
                session.touch();
                this.onChange(session);
            }
        } catch (InvalidSessionException e) {
            //
        }
    }

    /**
     * 修改会话的属性值
     *
     * @param sessionKey   sessionKey
     * @param attributeKey attributeKey
     * @param value        value
     */
    @Override
    public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) {
        try {
            super.setAttribute(sessionKey, attributeKey, value);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
        }
    }

    @Override
    public Session start(SessionContext context) {
        try {
            return super.start(context);
        } catch (Exception e) {
            return StaticSession.getInstance();
        }
    }

    @Override
    public void validateSessions() {
        super.validateSessions();
    }

    @Override
    public void stop(SessionKey key) {
        try {
            super.stop(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
        }
    }

    @Override
    public Date getStartTimestamp(SessionKey key) {
        try {
            return super.getStartTimestamp(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
            return new Date();
        }
    }

    @Override
    public void setTimeout(SessionKey key, long maxIdleTimeInMillis) {
        try {
            super.setTimeout(key, maxIdleTimeInMillis);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
        }
    }

    @Override
    protected Session retrieveSession(SessionKey sessionKey) {
        try {
            return super.retrieveSession(sessionKey);
        } catch (UnknownSessionException e) {
            // 获取不到SESSION不抛出异常
            return null;
        }
    }

    @Override
    public String getHost(SessionKey key) {
        try {
            return super.getHost(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
            return null;
        }
    }

    @Override
    public Object removeAttribute(SessionKey sessionKey, Object attributeKey) {
        try {
            return super.removeAttribute(sessionKey, attributeKey);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
            return null;
        }
    }

    /**
     * 从Request和cookie中获取sessionId
     *
     * @param request  request
     * @param response response
     * @return sessionId
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        // 如果参数中包含“__sid”参数，则使用此sid会话。 例如：http://localhost/project?__sid=xxx&__cookie=true
        String sid = request.getParameter("__sid");
        if (StringUtils.isNotBlank(sid)) {
            // 是否将sid保存到cookie，浏览器模式下使用此参数。
            if (WebUtils.isTrue(request, "__cookie")) {
                HttpServletRequest rq = (HttpServletRequest) request;
                HttpServletResponse rs = (HttpServletResponse) response;
                Cookie template = getSessionIdCookie();
                Cookie cookie = new SimpleCookie(template);
                cookie.setValue(sid);
                cookie.saveTo(rq, rs);
            }
            // 设置当前session状态
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
                    ShiroHttpServletRequest.URL_SESSION_ID_SOURCE);
            // session来源与url
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sid);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sid;
        } else {
            return super.getSessionId(request, response);
        }
    }

    @Override
    public long getTimeout(SessionKey key) {
        try {
            return super.getTimeout(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
            return 0L;
        }
    }

    @Override
    public Date getLastAccessTime(SessionKey key) {
        try {
            return super.getLastAccessTime(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
            return null;
        }
    }

    @Override
    public Collection<Object> getAttributeKeys(SessionKey key) {
        try {
            return super.getAttributeKeys(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
            return null;
        }
    }

    @Override
    public void checkValid(SessionKey key) {
        try {
            super.checkValid(key);
        } catch (InvalidSessionException e) {
            // 获取不到SESSION不抛出异常
        }
    }

    @Override
    protected Session doCreateSession(SessionContext context) {
        try {
            return super.doCreateSession(context);
        } catch (IllegalStateException e) {
            return null;
        }
    }
}