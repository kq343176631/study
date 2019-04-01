package com.style.admin.modules.sys.service;

import com.style.admin.modules.sys.entity.SysMenu;
import com.style.admin.modules.sys.entity.SysRole;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.common.service.CrudService;

import java.util.List;

public interface SysUserService extends CrudService<SysUser> {

    SysUser getUserByLoginName(String loginName);

    List<SysRole> getSysRoleList(String loginName);

    List<SysMenu> getSysMenuList(String loginName);
}
