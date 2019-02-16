package com.style.common.lang;

import com.style.common.TimeUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 对象操作工具类, 继承org.apache.commons.lang3.ObjectUtils类
 */
@SuppressWarnings("all")
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    /**
     * 转换为Double类型
     */
    public static Double toDouble(final Object object) {
        if (object == null) {
            return 0D;
        }
        try {
            return NumberUtils.toDouble(StringUtils.trim(object.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(final Object object) {
        return toDouble(object).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(final Object object) {
        return toDouble(object).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(final Object object) {
        return toLong(object).intValue();
    }

    /**
     * 转换为Boolean类型
     * 'true', 'on', 'y', 't', 'yes' or '1' (case insensitive) will return true. Otherwise, false is returned.
     */
    public static Boolean toBoolean(final Object object) {
        if (object == null) {
            return false;
        }
        return BooleanUtils.toBoolean(object.toString()) || "1".equals(object.toString());
    }

    public static String toString(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Collection) {
            return object.toString();
        }
        return BeanUtils.toMap(object).toString();
    }

    /**
     * 空转空字符串
     * （"" to "" ; null to "" ; "null" to "" ; "NULL" to "" ; "Null" to ""）
     *
     * @param val 需转换的值
     * @return 返回转换后的值
     */
    public static String toStringIgnoreNull(final Object val) {
        return ObjectUtils.toStringIgnoreNull(val, StringUtils.EMPTY);
    }

    /**
     * 空对象转空字符串
     * （"" to defaultVal ; null to defaultVal ; "null" to defaultVal ; "NULL" to defaultVal ; "Null" to defaultVal）
     *
     * @param val        需转换的值
     * @param defaultVal 默认值
     * @return 返回转换后的值
     */
    public static String toStringIgnoreNull(final Object val, String defaultVal) {
        if (null == val) {
            return "null";
        }
        String str = val.toString();
        return !"".equals(str) && !"null".equals(str.trim().toLowerCase()) ? str : defaultVal;
    }

    /**
     * 对象非空判断
     */
    public static boolean isNotEmpty(Object obj) {
        return !ObjectUtils.isEmpty(obj);
    }

    /**
     * 对象空判断
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        return false;
    }

    /**
     * 序列化对象
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        byte[] bytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long totalTime = System.currentTimeMillis() - beginTime;
        if (totalTime > 3000) {
            System.out.println("Serialize time: " + TimeUtils.formatDateAgo(totalTime));
        }
        return bytes;
    }

    /**
     * 反序列化对象
     */
    public static Object unSerialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        long beginTime = System.currentTimeMillis();
        Object object = null;
        if (bytes.length > 0) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                object = ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long totalTime = System.currentTimeMillis() - beginTime;
        if (totalTime > 3000) {
            System.out.println("UnSerialize time: " + TimeUtils.formatDateAgo(totalTime));
        }
        return object;
    }

}
