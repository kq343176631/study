package com.style.study.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理器
 */
public class ThreadPoolManager {

    private static Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);

    private static ThreadPoolManager instance = new ThreadPoolManager();

    /**
     * 线程池中：核心线程数量
     * 当正在运行的线程数量大于核心线程数量时，则创建新的线程去执行任务。
     */
    private static final int CORE_THREAD_NUM = 30;

    /**
     * 线程池中：最大线程数量
     */
    private static final int MAX_THREAD_NUM = 50;

    /**
     * 该线程池中非核心线程闲置超时时长
     */
    private static final int KEEP_ALIVE_TIME = 1000;

    private static ThreadPoolExecutor threadPool;
    /**
     * 该线程池中的任务队列：维护着等待执行的Runnable对象
     */
    private static final BlockingQueue<Runnable> workQueue;

    static {
        workQueue = new LinkedBlockingDeque<Runnable>(100);
        threadPool = new ThreadPoolExecutor(CORE_THREAD_NUM,
                MAX_THREAD_NUM,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                workQueue);
    }

    private ThreadPoolManager() {

    }

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    public void addSingleTask(Runnable task) {
        try {
            threadPool.execute(task);
        } catch (Exception e) {
            logger.error("thread execute task:", e);
        }

    }
}
