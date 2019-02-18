package com.style.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.style.common.constant.PageConstants;
import com.style.common.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected <T> IPage getPage(HttpServletRequest request) {

        //默认分页参数
        long pageNo = 1;
        long pageSize = 10;

        if (request == null) {
            return new Page(pageNo, pageSize).setAsc(PageConstants.DEFAULT_ORDER_BY);
        }

        //解析分页参数
        String strPageNo = request.getParameter(PageConstants.PAGE_NO);
        String strPageSize = request.getParameter(PageConstants.PAGE_SIZE);
        if (StringUtils.isNumeric(strPageNo)) {
            pageNo = Long.parseLong(strPageNo);
        }
        if (StringUtils.isNumeric(strPageSize)) {
            pageSize = Long.parseLong(strPageSize);
        }

        //分页对象
        Page<T> page = new Page<>(pageNo, pageSize);

        //排序字段
        String orderBy = request.getParameter(PageConstants.ORDER_BY);
        String order = request.getParameter(PageConstants.ORDER);
        //前端排序
        if (StringUtils.isNotEmpty(orderBy) && StringUtils.isNotEmpty(order)) {
            if (PageConstants.ASC.equalsIgnoreCase(order)) {
                return page.setAsc(orderBy);
            } else {
                return page.setDesc(orderBy);
            }
        } else {
            //默认排序
            page.setAsc(PageConstants.DEFAULT_ORDER_BY);
        }
        return page;
    }

}
