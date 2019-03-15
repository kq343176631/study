package com.style.admin.modules.security.authc;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

import java.util.Map;

/**
 * 单点登录令牌
 */
public class CasToken implements RememberMeAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private String ticket;

    private String loginName;

    // 登录附加参数
    private Map<String, Object> params;

    private boolean isRememberMe = false;

    public CasToken(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public Object getPrincipal() {
        return this.getLoginName();
    }

    @Override
    public Object getCredentials() {
        return this.ticket;
    }

    @Override
    public boolean isRememberMe() {
        return this.isRememberMe;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setRememberMe(boolean isRememberMe) {
        this.isRememberMe = isRememberMe;
    }
}
