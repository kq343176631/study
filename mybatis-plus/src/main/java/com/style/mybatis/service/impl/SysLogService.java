package com.style.mybatis.service.impl;

import com.style.web.dao.SysLogDao;
import com.style.mybatis.entity.SysLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysLogService extends BaseServiceImpl<SysLogDao,SysLog> {

    @Transactional(rollbackFor = Exception.class)
    public void save(SysLog entity) {
        insert(entity);
    }

}
