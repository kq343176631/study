package com.style.common.core;

import com.style.common.core.SpringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 国际化
 */
public class MsgUtils {

    private static MessageSource messageSource = (MessageSource) SpringUtils.getBean("messageSource");

    public static String getMessage(String code) {
        return getMessage(code, new String[0]);
    }

    public static String getMessage(String code, String... params) {
        return messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
    }
}
