package com.style.common;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SystemUtils extends org.apache.commons.lang3.SystemUtils {

    /**
     * 过期时间
     */
    private final long period;

    /**
     * 当前时间毫秒数
     */
    private final AtomicLong now;

    private SystemUtils(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    /**
     * 获取当前时间的毫秒数
     *
     * @return 当前时间毫秒数
     */
    public static long now() {
        return getInstance().now.get();
    }

    /**
     * 获取当前日期的字符串形式
     *
     * @return 当前时间毫秒数
     */
    public static String nowDate() {
        return new Timestamp(getInstance().now.get()).toString();
    }

    /**
     * 定时任务，更新当前时间，适用于高并发。
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取当前实例，单列，懒加载
     *
     * @return 当前对象实例
     */
    private static SystemUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 静态内部类，当前实例持有者，线程安全。
     */
    private static class InstanceHolder {

        public static final SystemUtils INSTANCE = new SystemUtils(1);

    }

    /**
     * 获得堆栈项
     *
     * @param i 第几个堆栈项
     * @return 堆栈项
     */
    public static StackTraceElement getStackTraceElement(int i) {
        StackTraceElement[] stackTrace = getStackElements();
        if (i < 0) {
            i += stackTrace.length;
        }
        return stackTrace[i];
    }

    /**
     * 获取当前栈信息
     *
     * @return 当前栈信息
     */
    public static StackTraceElement[] getStackElements() {
        return Thread.currentThread().getStackTrace();
    }

    /**
     * 获取入口堆栈信息
     *
     * @return 入口堆栈信息
     * @since 4.1.4
     */
    public static StackTraceElement getRootStackElement() {
        final StackTraceElement[] stackElements = getStackElements();
        return stackElements[stackElements.length - 1];
    }
}
