package com.style.utils.collect;

import com.style.utils.lang.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 集合工具类
 */
public class CollectUtils extends org.apache.commons.collections.CollectionUtils {

    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirstElement(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLastElement(Collection<T> collection) {
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
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    public static String toString(final Collection collection, final String separator) {
        return StringUtils.join(collection, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String
     * 每个元素的前面加入prefix，后面加入suffix，如<div>myMessage</div>。
     */
    public static String toString(final Collection collection, final String prefix, final String suffix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(suffix);
        }
        return builder.toString();
    }

}
