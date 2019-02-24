package com.style.common.log.service.impl;

import com.style.common.crud.service.impl.BaseServiceImpl;
import com.style.common.log.dao.SysLogDao;
import com.style.common.log.entity.SysLog;
import com.style.common.log.service.SysLogService;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogDao,SysLog> implements SysLogService {

}
