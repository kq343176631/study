package com.style.admin.modules.log.service.impl;

import com.style.common.crud.service.impl.BaseServiceImpl;
import com.style.admin.modules.log.dao.SysLogDao;
import com.style.admin.modules.log.entity.SysLog;
import com.style.admin.modules.log.service.SysLogService;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogDao,SysLog> implements SysLogService {

}
