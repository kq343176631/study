package com.style.admin.modules.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.log.dao.SysLogErrorDao;
import com.style.admin.modules.log.entity.SysLogError;
import com.style.admin.modules.log.service.SysLogErrorService;
import com.style.common.service.impl.CrudServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysLogErrorServiceImpl extends CrudServiceImpl<SysLogErrorDao, SysLogError> implements SysLogErrorService {

    @Override
    protected QueryWrapper<SysLogError> getWrapper(Map<String, Object> params) {
        return null;
    }
}
