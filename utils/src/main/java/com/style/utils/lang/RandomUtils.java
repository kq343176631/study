package com.style.utils.lang;

/**
 * 随机数工具类
 */
public class RandomUtils extends org.apache.commons.lang3.RandomUtils {

    public static int nextInt(int num) {
        return nextInt(0, num);
    }
}
