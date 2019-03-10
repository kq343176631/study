package com.style.admin.modules.msg.dao;

import com.style.admin.modules.msg.entity.SysMailLog;
import com.style.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件发送记录
 */
@Mapper
public interface SysMailLogDao extends BaseDao<SysMailLog> {

}
