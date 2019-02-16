package com.style.common.lang;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean 工具类
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

    /**
     * 将Map转换为Object
     *
     * @param map   待转换Map
     * @param clazz 目标对象的类
     */
    public static <T, V> T toObject(Map<String, V> map, Class<T> clazz) {
        return toObject(map, clazz, false);
    }

    /**
     * 将Map转换为Object
     *
     * @param map    待转换Map
     * @param object 目标对象的类
     */
    public static <T, V> T toObject(Map<String, V> map, T object) {
        return toObject(map, object, false);
    }

    /**
     * 将Map转换为Object
     *
     * @param clazz       目标对象的类
     * @param map         待转换Map
     * @param toCamelCase 是否去掉下划线
     */
    public static <T, V> T toObject(Map<String, V> map, Class<T> clazz, boolean toCamelCase) {
        try {
            return toObject(map, clazz.newInstance(), toCamelCase);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将Map转换为Object
     *
     * @param object      目标对象的类
     * @param map         待转换Map
     * @param toCamelCase 是否采用驼峰命名法转换
     */
    public static <T, V> T toObject(Map<String, V> map, T object, boolean toCamelCase) {
        if (toCamelCase) {
            map = MapUtils.toCamelCaseMap(map);
        }
        try {
            populate(object, map);
        } catch (Exception e) {
            return null;
        }
        return object;
    }

    /**
     * List<Map<String, V>转换为List<T>
     *
     * @param mapList mapList
     * @param clazz   clazz
     */
    public static <T, V> List<T> toObjectList(List<HashMap<String, V>> mapList, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (CollectUtils.isNotEmpty(mapList)) {
            for (HashMap<String, V> map : mapList) {
                result.add(toObject(map, clazz));
            }
        }
        return result;
    }

    /**
     * 对象转Map
     *
     * @param object 目标对象
     * @return 转换出来的值都是String
     */
    public static Map<String, String> toMap(Object object) {
        try {
            return describe(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转Map
     *
     * @param object 目标对象
     * @return 转换出来的值类型是原类型
     */
    public static Map<String, Object> toNativeMap(Object object) {
        try {
            return PropertyUtils.describe(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转换为Collection<Map<K, V>>
     *
     * @param list 待转换对象集合
     */
    public static <T> List<Map<String, String>> toMapList(List<T> list) {
        List<Map<String, String>> result = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (T object : list) {
                result.add(toMap(object));
            }
        }
        return result;
    }
}
