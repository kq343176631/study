package com.style.admin.modules.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.log.dao.SysLogOperateDao;
import com.style.admin.modules.log.entity.SysLogOperate;
import com.style.admin.modules.log.service.SysLogOperateService;
import com.style.common.service.impl.CrudServiceImpl;

import java.util.Map;

public class SysLogOperateServiceImpl extends CrudServiceImpl<SysLogOperateDao,SysLogOperate> implements SysLogOperateService {

    @Override
    protected QueryWrapper<SysLogOperate> getWrapper(Map<String, Object> params) {
        return new QueryWrapper<>();
    }

}
