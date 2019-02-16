package com.style.common.reflect;

import com.style.common.lang.ListUtils;
import com.style.common.reflect.vfs.VFS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassUtils
 */
public class ClassUtils extends org.apache.commons.lang3.ClassUtils {

    private static final Log logger = LogFactory.getLog(ClassUtils.class);

    /**
     * 获取当前对象的 class
     *
     * @param object object
     * @return 如果是代理的class，返回父 class，否则返回自身
     */
    public static Class<?> getUserClass(Object object) {
        if (object == null) {
            return null;
        }
        Class clazz = object.getClass();
        if (isProxy(clazz)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处 如无法找到, 返回Object.class.
     */
    public static Class getSuperClassGenericType(final Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.如无法找到, 返回Object.class.
     */
    public static Class getSuperClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            logger.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            logger.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index, clazz.getSimpleName(), params.length));
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(String.format("Warn: %s not set the actual class on superclass generic parameter",
                    clazz.getSimpleName()));
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 判断是否为代理对象
     *
     * @param clazz 传入 class 对象
     * @return 如果对象class是代理 class，返回 true
     */
    public static boolean isProxy(Class<?> clazz) {
        return clazz != null && clazz.getName().contains("$$");
    }

    /**
     * 在指定包下，查找指定类（parent）的实现类（排除当前类和抽象类）。
     */
    public static <T> Set<Class<?>> searchSubClass(Class<?> parent, String... packageNames) {
        if (packageNames != null) {
            for (String pkg : packageNames) {
                List<String> classFiles = searchClassFiles(pkg);
                if (classFiles != null) {
                    Set<Class<?>> matches = new HashSet<>();
                    for (String path : classFiles) {
                        String fullClassName = path.substring(0, path.indexOf('.')).replace('/', '.');
                        try {
                            Class<?> child = ClassUtils.class.getClassLoader().loadClass(fullClassName);
                            if (isSubClass(child, parent)) {
                                matches.add(child);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                    return matches;
                }
            }
        }
        return new HashSet<>();
    }

    /**
     * 在指定包下，查找持有指定注解（annotation）的类。
     */
    public static Set<Class<?>> searchAnnotatedClass(Class<? extends Annotation> annotation, String... packageNames) {
        if (packageNames != null) {
            for (String pkg : packageNames) {
                List<String> classFiles = searchClassFiles(pkg);
                if (classFiles != null) {
                    Set<Class<?>> matches = new HashSet<>();
                    for (String path : classFiles) {
                        String fullClassName = path.substring(0, path.indexOf('.')).replace('/', '.');
                        try {
                            Class<?> clazz = ClassUtils.class.getClassLoader().loadClass(fullClassName);
                            if (hasAnnotation(clazz, annotation)) {
                                matches.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                    return matches;
                }
            }
        }
        return new HashSet<>();
    }

    /**
     * 判断指定类是否为继承于当前类（不包含当前类，不包含抽象类）
     */
    public static boolean isSubClass(Class<?> child, Class<?> parent) {
        return child != null && parent.isAssignableFrom(child)    // 是继承与该类
                && !child.getName().equals(parent.getName())     // 不包含当前类
                && !Modifier.isAbstract(child.getModifiers());   // 不包含抽象类;
    }

    /**
     * 判断一个类是否拥有指定注解。
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return clazz != null && clazz.isAnnotationPresent(annotation);
    }

    /**
     * 列出指定包下的所有文件（.class）。
     */
    public static List<String> searchClassFiles(String packageName) {
        String path = getPackagePath(packageName);
        try {
            VFS vfs = VFS.getInstance();
            if (vfs != null) {
                List<String> result = ListUtils.newArrayList();
                for (String child : vfs.list(path)) {
                    if (child.endsWith(".class")) {
                        result.add(child);
                    }
                }
                return result;
            }
        } catch (IOException e) {
            logger.error("Could not read package: " + packageName, e);
        }
        return null;
    }

    /**
     * 将包名转换为路径
     */
    protected static String getPackagePath(String packageName) {
        return packageName == null ? null : packageName.replace('.', '/');
    }
}

