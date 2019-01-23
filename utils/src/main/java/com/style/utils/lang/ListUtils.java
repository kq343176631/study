package com.style.utils.lang;

import com.style.utils.reflect.ReflectUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * List工具类
 */
public class ListUtils extends org.apache.commons.collections.ListUtils {

    /**
     * ArrayList
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        CollectUtils.addAll(list, elements);
        return list;
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> iterable) {
        ArrayList<E> list = newArrayList();
        addAll(list, iterable);
        return list;
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iterator) {
        ArrayList<E> list = newArrayList();
        addAll(list, iterator);
        return list;
    }

    /**
     * LinkedList
     */
    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<>();
    }

    public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> iterable) {
        LinkedList<E> list = newLinkedList();
        addAll(list, iterable);
        return list;
    }

    public static <E> LinkedList<E> newLinkedList(Iterator<? extends E> iterator) {
        LinkedList<E> list = newLinkedList();
        addAll(list, iterator);
        return list;
    }

    /**
     * CopyOnWriteArrayList
     */
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<>();
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> iterable) {
        CopyOnWriteArrayList<E> list = newCopyOnWriteArrayList();
        addAll(list, iterable);
        return list;
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterator<? extends E> iterator) {
        CopyOnWriteArrayList<E> list = newCopyOnWriteArrayList();
        addAll(list, iterator);
        return list;
    }

    /**
     * 是否包含字符串
     *
     * @param str     验证字符串
     * @param strList 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, List<String> strList) {
        if (str != null && strList != null) {
            for (String s : strList) {
                if (str.equals(StringUtils.trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回a+b的新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }

    /**
     * 返回a-b的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<>(a);
        for (T element : b) {
            list.remove(element);
        }
        return list;
    }

    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<>();
        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * 本地列表排序
     *
     * @param list    需要排序的列表
     * @param orderBy 排序的键值（如：id desc）
     */
    public static <T> List<T> listOrderBy(List<T> list, String orderBy) {
        if (list != null && StringUtils.isNotBlank(orderBy)) {
            final String[] ss = orderBy.trim().split("");
            final String t = ss.length == 2 ? ss[1] : "asc";
            list.sort(new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    String s1, s2;
                    if (o1 instanceof Map) {
                        s1 = ObjectUtils.toString(((Map) o1).get(ss[0]));
                        s2 = ObjectUtils.toString(((Map) o2).get(ss[0]));
                    } else {
                        s1 = ObjectUtils.toString(ReflectUtils.invokeGetter(o1, ss[0]));
                        s2 = ObjectUtils.toString(ReflectUtils.invokeGetter(o2, ss[0]));
                    }
                    if ("asc".equalsIgnoreCase(t)) {
                        return s1.compareTo(s2);
                    } else {
                        return s2.compareTo(s1);
                    }
                }
            });
        }
        return list;
    }

    /**
     * 将列表转换为数组
     *
     * @param list  list
     * @param array array
     * @param <T>   t
     * @return arrays
     */
    public static <T> T[] toArray(List<T> list, T[] array) {
        return list.toArray(array);
    }

    /**
     * 将迭代器中的元素添加到集合中
     *
     * @param target   collection
     * @param iterator iterator
     * @param <T>      泛型
     * @return boolean
     */
    private static <T> boolean addAll(Collection<T> target, Iterator<? extends T> iterator) {
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= target.add(iterator.next());
        }
        return wasModified;
    }

    /**
     * 将可迭代器的元素添加到集合中
     *
     * @param target   collection
     * @param iterable iterableImpl
     * @param <T>      泛型
     * @return boolean
     */
    @SuppressWarnings("all")
    public static <T> boolean addAll(Collection<T> target, Iterable<? extends T> iterable) {
        if (iterable instanceof Collection) {
            Collection<? extends T> c = cast(iterable);
            return target.addAll(c);
        }
        return addAll(target, iterable.iterator());
    }

    /**
     * 将Iterable的实现类转换为集合
     *
     * @param iterable iterableImpl
     * @param <T>      泛型
     * @return Collection
     */
    private static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }
}
