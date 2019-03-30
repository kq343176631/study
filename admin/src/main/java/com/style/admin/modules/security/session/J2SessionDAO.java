package com.style.admin.modules.security.session;

import com.style.common.web.servlet.ServletUtils;
import com.style.utils.collect.ListUtils;
import com.style.utils.lang.DateUtils;
import com.style.utils.lang.ObjectUtils;
import com.style.utils.lang.StringUtils;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 将会话存储到Redis中
 * 不需要缓存管理器
 */
public class J2SessionDAO extends AbstractSessionDAO implements SessionDAO {

    private String region = "sys_session";

    private CacheChannel channel;

    public J2SessionDAO(CacheChannel channel) {
        this.channel = channel;
    }

    /**
     * 创建会话
     *
     * @param session session
     * @return sessionId
     */
    @Override
    public Serializable create(Session session) {
        return this.doCreate(session);
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.update(session);
        return sessionId;
    }

    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        try {
            return this.doReadSession(sessionId);
        } catch (UnknownSessionException e) {
            return null;
        }
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        // 从REQUEST中获取session
        Session s = null;
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request != null) {
            // 如果是静态文件，则不获取SESSION
            if (ServletUtils.isStaticFile(request.getServletPath())) {
                return StaticSession.getInstance();
            }
            s = (Session) request.getAttribute("session_" + sessionId);
        }
        if (s != null) {
            return s;
        }
        // 从REDIS中获取SESSION
        Session session = (Session) this.channel.get(this.region, ObjectUtils.toString(sessionId)).getValue();
        // 将会话放到请求中
        if (request != null && session != null) {
            request.setAttribute("session_" + sessionId, session);
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            return;
        }
        if (session instanceof ValidatingSession && ((ValidatingSession) session).isValid()) {
            this.channel.set(this.region, ObjectUtils.toString(session.getId()), session);
        } else {
            this.channel.evict(this.region, ObjectUtils.toString(session.getId()));
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        this.channel.evict(this.region, ObjectUtils.toString(session.getId()));
    }

    /**
     * 获取所有在线会话
     */
    @Override
    public Collection<Session> getActiveSessions() {
        List<Session> sessions = ListUtils.newArrayList();
        Collection<String> keys = this.channel.keys(this.region);
        if (keys != null && keys.size() > 0) {
            Map<String, CacheObject> cacheMap = this.channel.get(this.region, keys);
            if (cacheMap != null && cacheMap.size() > 0) {
                cacheMap.forEach((key, value) -> {
                    Session session = (Session) value.getValue();
                    if (session == null) {
                        this.channel.evict(this.region, key);
                    } else {
                        sessions.add(session);
                    }
                });
            }
        }
        return sessions;
    }

    @Override
    public Collection<Session> getActiveSessions(boolean includeLeave, Object principal, Session excludeSession) {
        return this.doFilterActiveSession(this.getActiveSessions(), includeLeave, principal, excludeSession);
    }

    @Override
    public Collection<Session> doFilterActiveSession(Collection<Session> activeSessions, boolean includeLeave, Object principal, Session excludeSession) {
        List<Session> filteredSession = ListUtils.newArrayList();
        if (activeSessions != null && activeSessions.size() > 0) {

            activeSessions.forEach(session -> {
                // 判断是否包含离线会话
                boolean require = includeLeave || DateUtils.pastMinutes(session.getLastAccessTime()) <= 3;

                // 筛选出当前身份的会话。
                if (principal != null) {
                    PrincipalCollection pc = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                    if (principal.toString().equals(pc != null ? pc.getPrimaryPrincipal().toString() : StringUtils.EMPTY)) {
                        require = true;
                    }
                }

                // 去除需要过滤的SESSION。
                if (excludeSession != null && excludeSession.getId().equals(session.getId())) {
                    require = false;
                }

                // 需要的会话
                if (require) {
                    filteredSession.add(session);
                }
            });
        }
        return filteredSession;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
