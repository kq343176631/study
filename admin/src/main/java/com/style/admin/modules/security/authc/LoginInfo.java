package com.style.admin.modules.security.authc;

import com.style.utils.collect.MapUtils;
import com.style.utils.lang.StringUtils;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;

/**
 * 登录信息（用户身份）
 */
public class LoginInfo implements Principal, Serializable {

    // 主键
    private String id;

    // 身份的唯一标识
    private String loginName;

    //  登录附加参数
    private Map<String, Object> params;

    private static final long serialVersionUID = 1L;

    public LoginInfo() {

    }

    public LoginInfo(String id, String loginName) {
        this(id, loginName, null);
    }

    public LoginInfo(String id, String loginName, Map<String, Object> params) {
        this.id = id;
        this.loginName = loginName;
        this.params = params;
    }

    public void setParamValue(String key, String value) {
        if (this.params == null) {
            this.params = MapUtils.newHashMap();
        }
        this.params.put(key, value);
    }

    public String getParamValue(String key) {
        if (this.params == null) {
            return null;
        }
        Object value = this.params.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public String getParamValue(String key, String defaultValue) {
        String value;
        return StringUtils.isNotBlank(value = this.getParamValue(key)) ? value : defaultValue;
    }


    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.getLoginName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            // 比较属性
            LoginInfo info = (LoginInfo) obj;
            if (this.getId() == null) {
                return info.getId() == null;
            } else {
                return this.getId().equals(info.getId());
            }
        }
    }

    @Override
    public String getName() {
        return this.getLoginName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.loginName);
    }

    @Override
    public String toString() {
        return this.getLoginName();
    }
}
