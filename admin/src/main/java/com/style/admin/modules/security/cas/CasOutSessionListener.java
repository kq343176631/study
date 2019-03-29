package com.style.admin.modules.security.cas;

/**
 * 单点退出监听器
 */

import com.style.utils.core.SpringUtils;
import org.jasig.cas.client.session.SessionMappingStorage;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public final class CasOutSessionListener implements HttpSessionListener {

    private CasOutHandler casOutHandler;

    public CasOutSessionListener() {
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
    }

    /**
     * 销毁会话
     *
     * @param event event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        this.getSessionMappingStorage().removeBySessionById(session.getId());
    }

    /**
     * 获取 SessionMappingStorage
     *
     * @return SessionMappingStorage
     */
    private SessionMappingStorage getSessionMappingStorage() {
        if (this.casOutHandler == null) {
            this.casOutHandler = SpringUtils.getBean(CasOutHandler.class);
        }
        return this.casOutHandler.getSessionMappingStorage();
    }
}

