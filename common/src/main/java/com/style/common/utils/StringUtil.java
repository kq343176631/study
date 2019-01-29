package com.style.common.utils;

public class StringUtil{

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

    public static boolean isBlank(final CharSequence cs){
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, true);
    }

    private static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == prefix;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return CharSequenceUtil.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(final CharSequence str, final CharSequence suffix, final boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == suffix;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        final int strOffset = str.length() - suffix.length();
        return CharSequenceUtil.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }
}
