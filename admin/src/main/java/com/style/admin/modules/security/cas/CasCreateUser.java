package com.style.admin.modules.security.cas;

import com.style.admin.modules.sys.entity.User;

import java.util.Map;

public interface CasCreateUser {
    void createUser(User user, Map<String, Object> attrs);
}
