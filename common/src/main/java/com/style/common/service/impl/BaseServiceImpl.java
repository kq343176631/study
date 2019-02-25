package com.style.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.service.BaseService;
import com.style.mybatis.injector.SqlMethod;
import com.style.utils.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;

import java.util.Map;

/**
 * BaseService
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    protected Class<?> modelClass = null;

    /**
     * 获取分页对象
     * @param params      分页查询参数
     * @param defaultOrderField  默认排序字段
     * @param isAsc              排序方式
     */
    protected Page<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {

        //分页参数
        long curPage = 1;
        long limit = 10;

        if(params.get(Constants.PAGE_NO) != null){
            curPage = Long.parseLong((String)params.get(Constants.PAGE_NO));
        }
        if(params.get(Constants.PAGE_SIZE) != null){
            limit = Long.parseLong((String)params.get(Constants.PAGE_SIZE));
        }

        //分页对象
        Page<T> page = new Page<>(curPage, limit);

        //分页参数
        params.put(Constants.PAGE_NO, page);

        //排序字段
        String orderField = (String)params.get(Constants.ORDER_FIELD);
        String order = (String)params.get(Constants.ORDER_METHOD);

        //前端字段排序
        if(StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)){
            if(Constants.ASC.equalsIgnoreCase(order)) {
                return page.setAsc(orderField);
            }else {
                return page.setDesc(orderField);
            }
        }

        //默认排序
        if(isAsc) {
            page.setAsc(defaultOrderField);
        }else {
            page.setDesc(defaultOrderField);
        }

        return page;
    }

    /**
     * 获取当前 modelClass
     */
    protected Class<?> getCurrentModelClass() {
        if (this.modelClass == null) {
            this.modelClass = ReflectionKit.getSuperClassGenericType(getClass(), 1);
        }
        return this.modelClass;
    }

    protected abstract QueryWrapper<T> getWrapper(Map<String, Object> params);

    /**
     * 判断数据库操作是否成功
     * 注意！！ 该方法为 Integer 判断，不可传入 int 基本类型
     */
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    /**
     * 批量操作 SqlSession
     */
    protected SqlSession openSqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(getCurrentModelClass());
    }

    /**
     * 释放 sqlSession
     */
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(getCurrentModelClass()));
    }

    /**
     * 获取SqlStatement
     */
    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(getCurrentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }
}
