package com.style.common.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.style.common.constant.Constants;
import com.style.common.utils.ServletUtils;
import com.style.utils.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 基本控制器
 */
public abstract class BaseController {

    /**
     * 获取分页对象
     */
    protected <T> IPage getPage() {
        HttpServletRequest request = ServletUtils.getRequest();
        //默认分页参数
        long pageNo = 1;
        long pageSize = 10;

        if (request == null) {
            return new Page(pageNo, pageSize).setAsc(Constants.DEFAULT_ORDER_BY);
        }

        //解析分页参数
        String strPageNo = request.getParameter(Constants.PAGE_NO);
        String strPageSize = request.getParameter(Constants.PAGE_SIZE);
        if (StringUtils.isNumeric(strPageNo)) {
            pageNo = Long.parseLong(strPageNo);
        }
        if (StringUtils.isNumeric(strPageSize)) {
            pageSize = Long.parseLong(strPageSize);
        }

        //分页对象
        Page<T> page = new Page<>(pageNo, pageSize);

        //排序字段
        String orderBy = request.getParameter(Constants.ORDER_BY);
        String order = request.getParameter(Constants.ORDER);
        //前端排序
        if (StringUtils.isNotEmpty(orderBy) && StringUtils.isNotEmpty(order)) {
            if (Constants.ASC.equalsIgnoreCase(order)) {
                return page.setAsc(orderBy);
            } else {
                return page.setDesc(orderBy);
            }
        } else {
            //默认排序
            page.setAsc(Constants.DEFAULT_ORDER_BY);
        }
        return page;
    }
}
