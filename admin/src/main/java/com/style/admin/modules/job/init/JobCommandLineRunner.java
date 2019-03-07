package com.style.admin.modules.job.init;

import com.style.admin.modules.job.dao.ScheduleJobDao;
import com.style.admin.modules.job.entity.ScheduleJobEntity;
import com.style.admin.modules.job.utils.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 初始化定时任务数据
 */
@Component
public class JobCommandLineRunner implements CommandLineRunner {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Override
    public void run(String... args) {
        List<ScheduleJobEntity> scheduleJobList = scheduleJobDao.list(null);
        for(ScheduleJobEntity scheduleJob : scheduleJobList){
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }
}