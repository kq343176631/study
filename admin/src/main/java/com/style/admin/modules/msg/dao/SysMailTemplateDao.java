package com.style.admin.modules.msg.dao;

import com.style.admin.modules.msg.entity.SysMailTemplateEntity;
import com.style.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板
 */
@Mapper
public interface SysMailTemplateDao extends BaseDao<SysMailTemplateEntity> {
	
}
