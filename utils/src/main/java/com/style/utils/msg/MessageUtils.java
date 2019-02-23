package com.style.utils.msg;

import com.style.utils.core.SpringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 国际化
 */
public class MessageUtils {

    private static MessageSource messageSource = (MessageSource) SpringUtils.getBean("messageSource");

    public static String getMessage(int code) {
        return getMessage(code, new String[0]);
    }

    public static String getMessage(int code, String... params) {
        return messageSource.getMessage(code + "", params, LocaleContextHolder.getLocale());
    }
}
