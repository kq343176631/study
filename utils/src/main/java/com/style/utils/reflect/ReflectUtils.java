package com.style.utils.reflect;

import com.style.utils.core.Constants;
import com.style.utils.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 反射工具类.
 */
@SuppressWarnings("all")
public class ReflectUtils {

    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     *
     * @param object    object
     * @param fieldName fieldName
     * @return
     */
    public static <E> E getFieldValue(final Object object, final String fieldName) {
        Field field = getAccessibleField(object, fieldName);
        if (field == null) {
            logger.debug("在 [" + object.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return null;
        }
        E result = null;
        try {
            result = (E) field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static <E> void setFieldValue(final Object object, final String fieldName, final E value) {
        Field field = getAccessibleField(object, fieldName);
        if (field == null) {
            logger.debug("在 [" + object.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return;
        }
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }
    }

    /**
     * 调用Setter方法, 仅匹配方法名。支持多级，如：对象名.对象名.方法
     *
     * @param object       object
     * @param propertyName propertyName
     * @param value        value
     */
    public static <E> void invokeSetter(Object object, String propertyName, E value) {
        Object result = object;
        String[] names = StringUtils.split(propertyName, Constants.POINT);
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                result = invokeMethod(result, Constants.GETTER_PREFIX + StringUtils.capitalize(names[i]));
            } else {
                invokeMethod(result, Constants.SETTER_PREFIX + StringUtils.capitalize(names[i]), value);
            }
        }
    }

    /**
     * 调用Getter方法。支持多级调用，如：对象名.对象名.方法
     *
     * @param object       对象
     * @param propertyName 属性名
     * @return result
     */
    public static <E> E invokeGetter(Object object, String propertyName) {
        Object result = object;
        for (String methodName : StringUtils.split(propertyName, Constants.POINT)) {
            result = invokeMethod(result, Constants.GETTER_PREFIX + StringUtils.capitalize(methodName));
        }
        return (E) result;
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     *
     * @param object     对象
     * @param methodName 方法名
     * @param args       参数
     * @return result
     */
    public static <E> E invokeMethod(final Object object, final String methodName, final Object... args) {
        Method method = getAccessibleMethod(object, methodName, getParamTypes(args));
        if (method == null) {
            return null;
        }
        try {
            return (E) method.invoke(object, args);
        } catch (Exception e) {
            String msg = "method: " + method + ", obj: " + object + ", args: " + args + "";
            throw reflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field getAccessibleField(final Object object, final String fieldName) {
        if (object == null) {
            return null;
        }
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     *
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getAccessibleMethod(final Object object, final String methodName, final Class<?>... parameterTypes) {
        if (object == null) {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = object.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public.
     * 尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     *
     * @param method methoda
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public.
     * 尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     *
     * @param field field
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     *
     * @param msg
     * @param e
     * @return
     */
    public static RuntimeException reflectionExceptionToUnchecked(String msg, Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(msg, e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(msg, ((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(msg, e);
    }

    /**
     * 参数数组转换为参数类对象数组
     *
     * @param args 参数数组
     * @return 参数类对象数组
     */
    private static Class<?>[] getParamTypes(Object... args) {
        if (args != null && args.length > 0) {
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            return paramTypes;
        }
        return null;
    }
}
