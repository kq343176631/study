package com.style.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Mark sunlightcs@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpsLog {

	String value() default "";
}
