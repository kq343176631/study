package com.style.common.validator;

import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.utils.lang.ArrayUtils;
import com.style.utils.collect.CollectUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 断言工具类
 */
public class AssertUtils {

    /**
     * 断言是否为空白
     */
    public static void isBlank(String str, String... params) {
        isBlank(str, ErrorCode.NOT_NULL, params);
    }

    /**
     * 断言是否为空白
     */
    public static void isBlank(String str, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if (StringUtils.isBlank(str)) {
            throw new ValidateException(code, params);
        }
    }

    /**
     * 断言是否为NULL
     */
    public static void isNull(Object object, String... params) {
        isNull(object, ErrorCode.NOT_NULL, params);
    }

    /**
     * 断言是否为NULL
     */
    public static void isNull(Object object, Integer code, String... params) {

        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if (object == null) {
            throw new ValidateException(code, params);
        }
    }

    /**
     * 断言数组是否为空
     */
    public static void isArrayEmpty(Object[] array, String... params) {
        isArrayEmpty(array, ErrorCode.NOT_NULL, params);
    }

    /**
     * 断言数组是否为空
     */
    public static void isArrayEmpty(Object[] array, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if(ArrayUtils.isEmpty(array)){
            throw new ValidateException(code, params);
        }
    }

    /**
     * 断言LIST是否为空
     */
    public static void isListEmpty(List<?> list, String... params) {
        isListEmpty(list, ErrorCode.NOT_NULL, params);
    }

    /**
     * 断言LIST是否为空
     */
    public static void isListEmpty(List<?> list, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if(CollectUtils.isEmpty(list)){
            throw new ValidateException(code, params);
        }
    }

    /**
     * 断言MAP是否为空
     */
    public static void isMapEmpty(Map map, String... params) {
        isMapEmpty(map, ErrorCode.NOT_NULL, params);
    }

    /**
     * 断言MAP是否为空
     */
    public static void isMapEmpty(Map map, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if(MapUtils.isEmpty(map)){
            throw new ValidateException(code, params);
        }
    }
}
