package com.style.admin.modules.msg.dao;

import com.style.admin.modules.msg.entity.SysSmsEntity;
import com.style.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信
 */
@Mapper
public interface SysSmsDao extends BaseDao<SysSmsEntity> {
	
}