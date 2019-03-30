package com.style.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.sys.dao.SysUserDao;
import com.style.admin.modules.sys.entity.SysUser;
import com.style.admin.modules.sys.service.SysUserService;
import com.style.common.service.impl.CrudServiceImpl;

import java.util.Map;

public class SysUserServiceImpl extends CrudServiceImpl<SysUserDao,SysUser> implements SysUserService {

    @Override
    protected QueryWrapper<SysUser> getWrapper(Map<String, Object> params) {
        return new QueryWrapper<>();
    }

    @Override
    public SysUser getUserByLoginName(String loginName) {
        return null;
    }
}
