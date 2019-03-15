package com.style.admin.modules.security.session;


import com.style.utils.collect.MapUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class StaticSession implements ValidatingSession, Serializable {

    private Serializable id = "1";

    private Date startTimestamp = new Date();

    private static final long serialVersionUID = 1L;

    private Map<Object, Object> attributes = MapUtils.newHashMap();

    private static final StaticSession instance = new StaticSession();

    private StaticSession() {
    }

    public Object getAttribute(Object key) throws InvalidSessionException {
        return this.attributes.get(key);
    }

    public void stop() throws InvalidSessionException {
    }

    public Date getStartTimestamp() {
        return this.startTimestamp;
    }

    public void touch() throws InvalidSessionException {
    }

    public boolean isValid() {
        return true;
    }

    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
    }

    public String getHost() {
        return null;
    }

    public Object removeAttribute(Object key) throws InvalidSessionException {
        return this.attributes.remove(key);
    }

    public Serializable getId() {
        return this.id;
    }

    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        this.attributes.put(key, value);
    }

    public Date getLastAccessTime() {
        return new Date();
    }

    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        return this.attributes.keySet();
    }

    public long getTimeout() throws InvalidSessionException {
        return 9223372036854775807L;
    }

    public void validate() throws InvalidSessionException {
    }

    public static StaticSession getInstance() {
        return instance;
    }
}

