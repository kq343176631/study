package com.style.common.crud.controller;

import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.web.ServletUtils;
import com.style.utils.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected <T> Page getPage() {

        HttpServletRequest request = ServletUtils.getHttpServletRequest();

        //默认分页参数
        long pageNo = 1;
        long pageSize = 10;

        if (request == null) {
            return new Page(pageNo, pageSize).setAsc(Constants.DEFAULT_ORDER_FIELD);
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
        String orderBy = request.getParameter(Constants.ORDER_FIELD);
        String order = request.getParameter(Constants.ORDER_METHOD);
        //前端排序
        if (StringUtils.isNotEmpty(orderBy) && StringUtils.isNotEmpty(order)) {
            if (Constants.ASC.equalsIgnoreCase(order)) {
                return page.setAsc(orderBy);
            } else {
                return page.setDesc(orderBy);
            }
        } else {
            //默认排序
            page.setAsc(Constants.DEFAULT_ORDER_FIELD);
        }
        return page;
    }

}
