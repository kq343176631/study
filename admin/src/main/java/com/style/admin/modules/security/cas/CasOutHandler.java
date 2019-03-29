package com.style.admin.modules.security.cas;

import com.style.admin.modules.security.authc.LoginInfo;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.common.constant.Constants;
import com.style.utils.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.session.HashMapBackedSessionMappingStorage;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 单点退出处理程序
 */
public final class CasOutHandler {

    private final Log log = LogFactory.getLog(this.getClass());

    // 单点退出参数名
    private String logoutParameterName = "logoutRequest";

    private String artifactParameterName = "ticket";

    private SessionMappingStorage sessionMappingStorage = new HashMapBackedSessionMappingStorage();

    public CasOutHandler() {
    }

    /**
     * 初始化
     */
    public void init() {
    }

    /**
     * 判断是否为单点退出请求
     *
     * @param request request
     * @return boolean
     */
    public boolean isCasLogoutRequest(HttpServletRequest request) {
        String logoutParameter = CommonUtils.safeGetParameter(request, this.logoutParameterName);
        return Constants.POST.equals(request.getMethod()) && !this.isMultipartRequest(request) && StringUtils.isNotBlank(logoutParameter);
    }

    /**
     * 记录session
     *
     * @param request request
     * @param ticket  ticket
     */
    public void recordSession(HttpServletRequest request, String ticket) {
        HttpSession session = request.getSession();
        String sessionMappingId;
        if (ticket != null) {
            sessionMappingId = ticket;
        } else {
            sessionMappingId = CommonUtils.safeGetParameter(request, this.artifactParameterName);
        }
        try {
            this.sessionMappingStorage.removeBySessionById(session.getId());
        } catch (Exception e) {
            this.log.debug(e.getMessage());
        }
        this.sessionMappingStorage.addSessionById(sessionMappingId, session);
    }

    /**
     * 销毁SESSION
     *
     * @param request       request
     * @param logoutRequest logoutRequest
     * @return User
     */
    public SysUser destroySession(HttpServletRequest request, String logoutRequest) {
        /*if (StringUtils.isBlank(logoutRequest)) {
            logoutRequest = CommonUtils.safeGetParameter(request, this.logoutParameterName);
        }
        User user = null;
        String mappingId = XmlUtils.getTextForElement(logoutRequest, this.artifactParameterName);
        HttpSession session;

        if (CommonUtils.isNotBlank(mappingId) && (session = this.sessionMappingStorage.removeSessionByMappingId(mappingId)) != null) {
            try {
                String principal;
                PrincipalCollection principalCollection = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                if (principalCollection != null) {
                    principal = (String) principalCollection.getPrimaryPrincipal();
                    user = UserUtils.getUserByLoginName(principal);
                }
                // 销毁SESSION
                session.invalidate();
                return user;
            } catch (IllegalStateException e) {
                this.log.debug("destroySession{}", e);
            }
        }
        return user;*/
        return null;
    }

    /**
     * 判读是否为多文件请求
     *
     * @param request request
     * @return boolean
     */
    private boolean isMultipartRequest(HttpServletRequest request) {
        if (!Constants.POST.equals(request.getMethod())) {
            return false;
        }
        String contentType = request.getContentType();
        //获取Content-Type
        return (contentType != null) && (contentType.toLowerCase().startsWith("multipart/"));
    }

    public void setArtifactParameterName(String name) {
        this.artifactParameterName = name;
    }

    public void setLogoutParameterName(String name) {
        this.logoutParameterName = name;
    }

    public LoginInfo destroySession(HttpServletRequest request) {
        this.destroySession(request, (String) null);
        return null;
    }

    public void recordSession(HttpServletRequest request) {
        this.recordSession(request, (String) null);
    }

    public boolean isTokenRequest(HttpServletRequest request) {
        return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.artifactParameterName));
    }

    public SessionMappingStorage getSessionMappingStorage() {
        return this.sessionMappingStorage;
    }

    public void setSessionMappingStorage(SessionMappingStorage storage) {
        this.sessionMappingStorage = storage;
    }

    public boolean isLogoutRequest(HttpServletRequest request) {
        return false;
    }
}
