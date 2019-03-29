package com.style.admin.modules.security.cas;

import com.style.admin.modules.sys.entity.SysUser;

import java.util.Map;

public interface CasCreateUser {
    void createUser(SysUser user, Map<String, Object> attrs);
}
