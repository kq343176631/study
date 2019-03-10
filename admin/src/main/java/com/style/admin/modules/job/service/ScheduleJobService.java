package com.style.admin.modules.job.service;

import com.style.admin.modules.job.entity.ScheduleJob;
import com.style.common.service.CrudService;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ScheduleJobService extends CrudService<ScheduleJob> {

    /**
     * 批量更新定时任务状态
     */
    int updateBatch(Long[] ids, int status);

    /**
     * 立即执行
     */
    void run(Long[] ids);

    /**
     * 暂停运行
     */
    void pause(Long[] ids);

    /**
     * 恢复运行
     */
    void resume(Long[] ids);
}
