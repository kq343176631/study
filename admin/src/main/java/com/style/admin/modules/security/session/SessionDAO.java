package com.style.admin.modules.security.session;

import org.apache.shiro.session.Session;

import java.util.Collection;

public interface SessionDAO extends org.apache.shiro.session.mgt.eis.SessionDAO {

    /**
     * 获取活动会话
     *
     * @param includeLeave   是否包含离线会话（最后访问时间大于3分钟为离线会话）
     * @param principal      根据登录者对象获取活动会话
     * @param excludeSession 需要过滤的会话
     * @return sessions
     */
    Collection<Session> getActiveSessions(boolean includeLeave, Object principal, Session excludeSession);

    /**
     * @param activeSessions 活动会话
     * @param includeLeave   是否包含离线会话（最后访问时间大于3分钟为离线会话）
     * @param principal      根据登录者对象获取活动会话
     * @param excludeSession 需要过滤的会话
     * @return sessions
     */
    Collection<Session> doFilterActiveSession(Collection<Session> activeSessions, boolean includeLeave, Object principal, Session excludeSession);

}
