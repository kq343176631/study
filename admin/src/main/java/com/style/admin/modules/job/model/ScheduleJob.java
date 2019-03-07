package com.style.admin.modules.job.model;

import com.style.admin.modules.job.entity.ScheduleJobEntity;
import com.style.admin.modules.job.utils.ScheduleUtils;
import com.style.utils.core.SpringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * 定时任务
 */
public class ScheduleJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void executeInternal(JobExecutionContext context) {
        ScheduleJobEntity scheduleJob = (ScheduleJobEntity) context.getMergedJobDataMap().
                get(ScheduleUtils.JOB_PARAM_KEY);

        //数据库保存执行记录
        /*ScheduleJobLogEntity log = new ScheduleJobLogEntity();
        log.setJobId(scheduleJob.getId());
        log.setBeanName(scheduleJob.getBeanName());
        log.setParams(scheduleJob.getParams());
		log.setCreateDate(new Date());*/

        //任务开始时间
        long startTime = System.currentTimeMillis();

        try {
            //执行任务
            logger.info("任务准备执行，任务ID：{}", scheduleJob.getId());
            Object target = SpringUtils.getBean(scheduleJob.getBeanName());
            Method method = target.getClass().getDeclaredMethod("run", String.class);
            method.invoke(target, scheduleJob.getParams());

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            //log.setTimes((int)times);
            //任务状态
            //log.setStatus(Constant.SUCCESS);

            logger.info("任务执行完毕，任务ID：{}  总共耗时：{} 毫秒", scheduleJob.getId(), times);
        } catch (Exception e) {
            logger.error("任务执行失败，任务ID：{}", scheduleJob.getId(), e);

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            //log.setTimes((int)times);

            //任务状态
            //log.setStatus(Constant.FAIL);
            //log.setError(ExceptionUtils.getErrorStackTrace(e));
        } finally {
            //获取spring bean
            //ScheduleJobLogService scheduleJobLogService = SpringContextUtils.getBean(ScheduleJobLogService.class);
            //scheduleJobLogService.insert(log);
        }
    }
}