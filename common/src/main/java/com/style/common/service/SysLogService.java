package com.style.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.style.common.dao.SysLogDao;
import com.style.common.entity.SysLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SysLogService extends BaseService<SysLogDao,SysLog> {

    @Transactional(rollbackFor = Exception.class)
    public void save(SysLog entity) {
        insert(entity);
    }

}
