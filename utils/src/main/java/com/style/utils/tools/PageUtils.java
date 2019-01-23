package com.style.utils.tools;

import com.style.utils.callback.MethodCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils {

    /**
     * 列表分页方法
     *
     * @param list         数据源
     * @param pageSize     每页大小
     * @param pageCallback 分页回调，返回当前页的数据及分页信息（pageList, pageNo, pageSize）
     * @author ThinkGem
     */
    public static <T> void pageList(List<T> list, int pageSize, MethodCallback pageCallback) {
        if (list != null && list.size() > 0) {
            int count = list.size(), pageNo = 1;
            int totalPage = (count + pageSize - 1) / pageSize;
            while (true) {
                // 执行回调，分页后的数据
                List<T> pageList = getPageList(list, pageNo, pageSize, totalPage);
                if (pageList.size() > 0) {
                    pageCallback.execute(pageList, pageNo, pageSize, totalPage);
                }
                // 如果为最后一页，则跳出循环
                if (pageNo >= totalPage) {
                    break;
                }
                // 页数加一继续下一页
                pageNo++;
            }
        }
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
}
