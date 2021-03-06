package com.style.utils.collect;

import com.style.utils.lang.ObjectUtils;
import com.style.utils.lang.StringUtils;
import com.style.utils.reflect.ReflectUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * List工具类
 */
public class ListUtils extends org.apache.commons.collections.ListUtils {

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
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, List<String> strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equals(StringUtils.trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        return (elements instanceof Collection) ? new ArrayList<>(cast(elements))
                : newArrayList(elements.iterator());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = newArrayList();
        addAll(list, elements);
        return list;
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<>();
    }

    public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements) {
        LinkedList<E> list = newLinkedList();
        addAll(list, elements);
        return list;
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<>();
    }

    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements) {
        Collection<? extends E> elementsCollection = (elements instanceof Collection)
                ? cast(elements) : newArrayList(elements);
        return new CopyOnWriteArrayList<>(elementsCollection);
    }

    private static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }

    private static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

    public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
        if (elementsToAdd instanceof Collection) {
            Collection<? extends T> c = cast(elementsToAdd);
            return addTo.addAll(c);
        }
        return addAll(addTo, elementsToAdd.iterator());
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtils.join(collection, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
     */
    public static String convertToString(final Collection collection, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }

    /**
     * 判断是否为空.
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断是否为不为空.
     */
    public static boolean isNotEmpty(Collection collection) {
        return !(isEmpty(collection));
    }

    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        //当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        //其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
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
     * 列表分页方法
     *
     * @param list      源数据
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @param totalPage 总页码数
     */
    private static <T> List<T> getPageList(List<T> list, int pageNo, int pageSize, int totalPage) {
        int fromIndex = 0; // 从哪里开始截取
        int toIndex = 0; // 截取几个
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        // 当前页小于或等于总页数时执行
        if (pageNo <= totalPage && pageNo != 0) {
            fromIndex = (pageNo - 1) * pageSize;
            if (pageNo == totalPage) {
                toIndex = list.size();
            } else {
                toIndex = pageNo * pageSize;
            }
        }
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 本地列表排序
     *
     * @param list    需要排序的列表
     * @param orderBy 排序的键值（如：id desc）
     * @author ThinkGem
     */
    public static <T> List<T> listOrderBy(List<T> list, String orderBy) {
        if (list != null && StringUtils.isNotBlank(orderBy)) {
            final String[] ss = orderBy.trim().split(" ");
            if (ss != null) {
                final String t = ss.length == 2 ? ss[1] : "asc";
                Collections.sort(list, new Comparator<T>() {
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
        }
        return list;
    }
}
