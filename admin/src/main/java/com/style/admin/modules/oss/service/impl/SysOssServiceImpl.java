package com.style.admin.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.oss.dao.SysOssDao;
import com.style.admin.modules.oss.entity.SysOssEntity;
import com.style.admin.modules.oss.service.SysOssService;
import com.style.common.service.impl.CrudServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class SysOssServiceImpl extends CrudServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

    @Override
    protected QueryWrapper<SysOssEntity> getWrapper(Map<String, Object> params) {
        return null;
    }
}
