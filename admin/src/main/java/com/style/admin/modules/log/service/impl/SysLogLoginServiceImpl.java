package com.style.admin.modules.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.log.dao.SysLogLoginDao;
import com.style.admin.modules.log.entity.SysLogLogin;
import com.style.admin.modules.log.service.SysLogLoginService;
import com.style.common.service.impl.CrudServiceImpl;

import java.util.Map;

public class SysLogLoginServiceImpl extends CrudServiceImpl<SysLogLoginDao,SysLogLogin> implements SysLogLoginService {

    @Override
    protected QueryWrapper<SysLogLogin> getWrapper(Map<String, Object> params) {
        return new QueryWrapper<>();
    }
}
