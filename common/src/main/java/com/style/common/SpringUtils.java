package com.style.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 工具类
 */
public class SpringUtils implements ApplicationContextAware, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(SpringUtils.class);

    /**
     * Spring Context
     */
    private static ApplicationContext context;

    /**
     * 获取 bean
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 获取 bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 获取 bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    /**
     * 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (context == null) {
            context = applicationContext;
        }
        logger.info(applicationContext.toString());
    }

    /**
     * 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() {
        clearHolder();
    }

    /**
     * 清除SpringUtils中的ApplicationContext为Null.
     */
    private static void clearHolder() {
        if (logger.isDebugEnabled()) {
            logger.debug("清除SpringContextUtils中的ApplicationContext:" + SpringUtils.context);
        }
        context = null;
    }
}