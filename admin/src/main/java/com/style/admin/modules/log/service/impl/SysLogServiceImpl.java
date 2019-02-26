package com.style.admin.modules.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.log.dao.SysLogDao;
import com.style.admin.modules.log.entity.SysLog;
import com.style.admin.modules.log.service.SysLogService;
import com.style.common.service.impl.CrudServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysLogServiceImpl extends CrudServiceImpl<SysLogDao, SysLog> implements SysLogService {

    @Override
    protected QueryWrapper<SysLog> getWrapper(Map<String, Object> params) {
        return null;
    }
}