package com.style.utils.lang;

import java.util.Arrays;
import java.util.List;

/**
 * 数组工具类
 */
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

    /**
     * 将数组转换为集合
     *
     * @param array array
     * @param <T>   T
     * @return list
     */
    public static <T> List<T> toList(T[] array) {
        return Arrays.asList(array);
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 数组
     * @return 数组对象内含有任意对象时返回 true
     * @see ArrayUtils#isEmpty(Object[])
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * 判断数据是否为空
     *
     * @param array 长度
     * @return 数组对象为null或者长度为 0 时，返回 false
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

}
