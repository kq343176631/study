package com.style.utils.tools;

import com.style.utils.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Html 工具类
 */
public class HtmlUtils {

    /**
     * 去除字符串中的标签
     * <p>abc</p> ==> abc
     *
     * @param tag 带标签的字符串
     * @return 字符串
     */
    public static String removeTag(String tag) {
        if (StringUtils.isBlank(tag)) {
            return StringUtils.EMPTY;
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(tag);
        return m.replaceAll(StringUtils.EMPTY);
    }
}
