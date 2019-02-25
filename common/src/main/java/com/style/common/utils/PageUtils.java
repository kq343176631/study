package com.style.common.utils;

import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.utils.lang.StringUtils;

import java.util.Map;

public class PageUtils {

    public static <T> Page<T> getPage(Map<String, Object> params) {
        return getPage(params, "update_date", true);
    }

    public static <T> Page<T> getPage(Map<String, Object> params, boolean isAsc) {
        return getPage(params, "update_date", isAsc);
    }

    /**
     * 获取分页对象
     *
     * @param params            分页查询参数
     * @param defaultOrderField 默认排序字段
     * @param isAsc             排序方式
     */
    public static <T> Page<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {

        //分页参数
        long curPage = 1;
        long limit = 10;

        if (params.get(Constants.PAGE_NO) != null) {
            curPage = Long.parseLong((String) params.get(Constants.PAGE_NO));
        }
        if (params.get(Constants.PAGE_SIZE) != null) {
            limit = Long.parseLong((String) params.get(Constants.PAGE_SIZE));
        }

        //分页对象
        Page<T> page = new Page<>(curPage, limit);

        //分页参数
        params.put(Constants.PAGE_NO, page);

        //排序字段
        String orderField = (String) params.get(Constants.ORDER_FIELD);
        String order = (String) params.get(Constants.ORDER_METHOD);

        //前端字段排序
        if (StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)) {
            if (Constants.ASC.equalsIgnoreCase(order)) {
                return page.setAsc(orderField);
            } else {
                return page.setDesc(orderField);
            }
        }

        //默认排序
        if (isAsc) {
            page.setAsc(defaultOrderField);
        } else {
            page.setDesc(defaultOrderField);
        }

        return page;
    }

}
