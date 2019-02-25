package com.style.common.service.impl;

import com.style.common.service.impl.BaseServiceImpl;
import com.style.common.dao.SysLogDao;
import com.style.common.entity.SysLog;
import com.style.common.service.SysLogService;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogDao,SysLog> implements SysLogService {

}
