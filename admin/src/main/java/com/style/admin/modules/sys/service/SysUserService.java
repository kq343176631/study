package com.style.admin.modules.sys.service;

import com.style.admin.modules.sys.entity.SysUser;
import com.style.common.service.CrudService;

public interface SysUserService extends CrudService<SysUser> {

    SysUser getUserByLoginName(String loginName);
}
