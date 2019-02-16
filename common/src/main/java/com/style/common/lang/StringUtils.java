package com.style.common.lang;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 字符串工具类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param args 字符串数组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... args) {
        if (str != null && args != null) {
            for (String s : args) {
                if (str.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param args 字符串数组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... args) {
        if (str != null && args != null) {
            for (String s : args) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 下划线转命名换为驼峰命名（首字母小写）
     * <p>
     * "hello_world" ==> "helloWorld"
     *
     * @param underline underline
     * @return camelCase
     */
    public static String toCamelCase(String underline) {
        if (underline == null) {
            return null;
        }
        underline = underline.toLowerCase();
        StringBuilder sb = new StringBuilder(underline.length());
        boolean upperCase = false;
        for (int i = 0; i < underline.length(); i++) {
            char c = underline.charAt(i);
            if (c == '_') {
                upperCase = i != 1;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线命名转换为驼峰命名（首字母大写）
     * <p>
     * "hello_world" ==> "HelloWorld"
     *
     * @param underline underline
     * @return camelCase
     */
    public static String toCapCamelCase(String underline) {
        if (underline == null) {
            return null;
        }
        underline = toCamelCase(underline);
        return underline.substring(0, 1).toUpperCase() + underline.substring(1);
    }

    /**
     * 驼峰命名转换为下划线命名（首字母大写）
     * <p>
     * "helloWorld" ==> "hello_world"
     * "HelloWorld" ==> "hello_world"
     *
     * @param cameCase cameCase
     * @return underline
     */
    public static String toUnderline(String cameCase) {
        if (cameCase == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < cameCase.length(); i++) {
            char c = cameCase.charAt(i);
            boolean nextUpperCase = true;
            if (i < (cameCase.length() - 1)) {
                nextUpperCase = Character.isUpperCase(cameCase.charAt(i + 1));
            }
            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append('_');
                }
                upperCase = true;
            } else {
                upperCase = false;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 获取随机字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String getRandomStr(int length) {
        char[] codeSeq = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);
            s.append(r);
        }
        return s.toString();
    }

    /**
     * 获取随机数字
     *
     * @param length length
     * @return 随机数字
     */
    public static String getRandomNum(int length) {
        char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);
            s.append(r);
        }
        return s.toString();
    }

    /**
     * 格式化字符串
     *
     * @param target 目标字符串
     * @param params format 参数
     * @return 格式化后的字符串
     */
    public static String format(String target, Object... params) {
        if (target.contains("%s") && ArrayUtils.isNotEmpty(params)) {
            return String.format(target, params);
        }
        return target;
    }

    /**
     * 将字节数组转换为字符串
     *
     * @param bytes bytes
     * @return 字符串
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }
}
