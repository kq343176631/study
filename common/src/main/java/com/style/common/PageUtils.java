package com.style.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils {

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
}
