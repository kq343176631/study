package com.style.admin.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.admin.modules.job.dao.ScheduleJobDao;
import com.style.admin.modules.job.entity.ScheduleJob;
import com.style.admin.modules.job.service.ScheduleJobService;
import com.style.admin.modules.job.utils.ScheduleUtils;
import com.style.common.constant.Constants;
import com.style.common.service.impl.CrudServiceImpl;
import com.style.utils.lang.StringUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScheduleJobServiceImpl extends CrudServiceImpl<ScheduleJobDao, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private Scheduler scheduler;

    @Override
    protected QueryWrapper<ScheduleJob> getWrapper(Map<String, Object> params) {

        String beanName = (String) params.get("beanName");
        QueryWrapper<ScheduleJob> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(beanName)) {
            wrapper.like("bean_name", beanName);
        }

        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ScheduleJob entity) {

        entity.setStatus(Constants.ScheduleStatus.NORMAL.getValue());
        ScheduleUtils.createScheduleJob(scheduler, entity);

        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ScheduleJob entity) {

        ScheduleUtils.updateScheduleJob(scheduler, entity);

        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(Collection<? extends Long> idList) {
        for (Long id : idList) {
            ScheduleUtils.deleteScheduleJob(scheduler, id);
        }

        //删除数据
        return super.deleteByIds(idList);
    }

    @Override
    public int updateBatch(Long[] ids, int status) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("ids", ids);
        map.put("status", status);
        return baseDao.updateBatch(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.run(scheduler, this.get(id));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pause(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.pauseJob(scheduler, id);
        }

        updateBatch(ids, Constants.ScheduleStatus.PAUSE.getValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resume(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.resumeJob(scheduler, id);
        }

        updateBatch(ids, Constants.ScheduleStatus.NORMAL.getValue());
    }

}