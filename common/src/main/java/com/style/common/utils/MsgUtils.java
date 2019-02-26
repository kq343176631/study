package com.style.common.utils;

import com.style.utils.core.SpringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 国际化
 */
public class MsgUtils {

    private static MessageSource messageSource = (MessageSource) SpringUtils.getBean("message");

    public static String getMessage(int code) {
        return getMessage(code, new String[0]);
    }

    public static String getMessage(int code, String... params) {
        return messageSource.getMessage(code + "", params, LocaleContextHolder.getLocale());
    }
}
