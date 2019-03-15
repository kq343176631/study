package com.style.admin.modules.security.authc;

import org.apache.shiro.authc.UsernamePasswordToken;

import java.util.Map;

/**
 * 表单登录令牌
 */
public class FormToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    // 登录附加参数
    private Map<String, Object> params;

    // 登录验证码
    private String captcha;

    public FormToken() {
    }

    public FormToken(String username, Map<String, Object> params) {
        this.setUsername(username);
        this.params = params;
    }

    public FormToken(String username, String password, boolean rememberMe, String host, String captcha, Map<String, Object> params) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
        this.params = params;
    }

    public FormToken(String username, char[] password, boolean rememberMe, String host, String captcha, Map<String, Object> params) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setPassword(String password) {
        this.setPassword(password != null ? password.toCharArray() : null);
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptcha() {
        return this.captcha;
    }
}
