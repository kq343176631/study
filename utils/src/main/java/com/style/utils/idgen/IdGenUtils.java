package com.style.utils.idgen;

import com.style.utils.code.CodeUtils;
import com.style.utils.lang.ObjectUtils;
import com.style.utils.lang.StringUtils;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 封装各种生成唯一性ID算法的工具类.
 */
public class IdGenUtils {

    private static SecureRandom random = new SecureRandom();

    private static IdWorker idWorker = new IdWorker(-1, -1);

    /**
     * 获取新唯一编号（18为数值）
     * 来自于twitter项目snowflake的id产生方案，全局唯一，时间有序。
     * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
     */
    public static String nextId() {
        return String.valueOf(idWorker.nextId());
    }

    /**
     * 生成UUID, 中间无-分割.
     */
    public static String uuid() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace("-", "");
    }

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }

    /**
     * 基于Base62编码的SecureRandom随机生成bytes.
     */
    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return CodeUtils.encodeBase62(randomBytes);
    }

    /**
     * 获取新代码编号
     */
    public static String nextCode(String code) {
        if (code != null) {
            String str = code.trim();
            int len = str.length() - 1;
            int lastNotNumIndex = 0;
            for (int i = len; i >= 0; i--) {
                if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
                    lastNotNumIndex = i;
                    break;
                }
            }
            // 如果最后一位是数字，并且last索引位置还在最后，则代表是纯数字，则最后一个不是数字的索引为-1
            if ((str.charAt(len) >= '0' && str.charAt(len) <= '9') && (lastNotNumIndex == len)) {
                lastNotNumIndex = -1;
            }
            String prefix = str.substring(0, lastNotNumIndex + 1);
            String numStr = str.substring(lastNotNumIndex + 1, str.length());
            long num = ObjectUtils.toLong(numStr);
            str = prefix + StringUtils.leftPad(String.valueOf(num + 1), numStr.length(), "0");
            return str;
        }
        return null;
    }
}
