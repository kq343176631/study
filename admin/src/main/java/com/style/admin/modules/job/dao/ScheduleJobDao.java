package com.style.admin.modules.job.dao;

import com.style.admin.modules.job.entity.ScheduleJobEntity;
import com.style.common.dao.BaseDao;
import com.style.mybatis.annotation.MyBatisPlus;

import java.util.Map;

/**
 * 定时任务
 *
 */
@MyBatisPlus
public interface ScheduleJobDao extends BaseDao<ScheduleJobEntity> {
	
	/**
	 * 批量更新状态
	 */
	int updateBatch(Map<String, Object> map);
}
