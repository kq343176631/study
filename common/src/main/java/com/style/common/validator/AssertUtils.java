package com.style.common.validator;

import com.style.common.constant.ErrorCode;
import com.style.common.exception.ValidateException;
import com.style.utils.lang.ArrayUtils;
import com.style.utils.collect.CollectUtils;
import com.style.utils.collect.MapUtils;
import com.style.utils.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class AssertUtils {

    public static void isBlank(String str, String... params) {
        isBlank(str, ErrorCode.NOT_NULL, params);
    }

    public static void isBlank(String str, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if (StringUtils.isBlank(str)) {
            throw new ValidateException(code, params);
        }
    }

    public static void isNull(Object object, String... params) {
        isNull(object, ErrorCode.NOT_NULL, params);
    }

    public static void isNull(Object object, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if (object == null) {
            throw new ValidateException(code, params);
        }
    }

    public static void isArrayEmpty(Object[] array, String... params) {
        isArrayEmpty(array, ErrorCode.NOT_NULL, params);
    }

    public static void isArrayEmpty(Object[] array, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if(ArrayUtils.isEmpty(array)){
            throw new ValidateException(code, params);
        }
    }

    public static void isListEmpty(List<?> list, String... params) {
        isListEmpty(list, ErrorCode.NOT_NULL, params);
    }

    public static void isListEmpty(List<?> list, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if(CollectUtils.isEmpty(list)){
            throw new ValidateException(code, params);
        }
    }

    public static void isMapEmpty(Map map, String... params) {
        isMapEmpty(map, ErrorCode.NOT_NULL, params);
    }

    public static void isMapEmpty(Map map, Integer code, String... params) {
        if(code == null){
            throw new ValidateException(ErrorCode.NOT_NULL, "code");
        }

        if(MapUtils.isEmpty(map)){
            throw new ValidateException(code, params);
        }
    }

}
