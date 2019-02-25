package com.style.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.style.common.service.BaseService;
import com.style.mybatis.injector.SqlMethod;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;

import java.util.Map;

/**
 * BaseService
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    protected Class<?> modelClass = null;

    /**
     * 获取条件构造器，默认用于List和Page
     */
    protected abstract QueryWrapper<T> getWrapper(Map<String, Object> params);

    /**
     * 获取当前 modelClass
     */
    protected Class<?> getCurrentModelClass() {
        if (this.modelClass == null) {
            this.modelClass = ReflectionKit.getSuperClassGenericType(getClass(), 1);
        }
        return this.modelClass;
    }

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
