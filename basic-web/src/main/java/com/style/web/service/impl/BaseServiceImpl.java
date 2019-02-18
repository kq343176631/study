package com.style.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.style.web.dao.BaseDao;
import com.style.web.entity.BaseEntity;
import com.style.mybatis.injector.SqlMethod;
import com.style.web.service.BaseService;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * BaseService
 */
public abstract class BaseServiceImpl<D extends BaseDao<T>, T extends BaseEntity<T>> implements BaseService<T> {

    @Autowired
    protected D baseDao;

    protected Class<?> modelClass = null;

    @Override
    public boolean insert(T entity) {
        return retBool(baseDao.insert(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<T> entityList) {
        return insertBatch(entityList, 100);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<T> entityList, int batchSize) {
        SqlSession batchSqlSession = openSqlSessionBatch();
        int i = 0;
        String sqlStatement = getSqlStatement(SqlMethod.INSERT);
        try {
            for (T anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        } finally {
            closeSqlSession(batchSqlSession);
        }
        return true;
    }

    @Override
    public boolean deleteById(String id) {
        return SqlHelper.delBool(baseDao.deleteById(id));
    }

    @Override
    public boolean deleteByIds(Collection<? extends String> idList) {
        return SqlHelper.delBool(baseDao.deleteByIds(idList));
    }

    @Override
    public boolean delete(Wrapper<T> wrapper) {
        return retBool(baseDao.delete(wrapper));
    }

    @Override
    public boolean updateById(T entity) {
        return retBool(baseDao.updateById(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, 30);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        SqlSession batchSqlSession = openSqlSessionBatch();
        int i = 0;
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        try {
            for (T anEntityList : entityList) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        } finally {
            closeSqlSession(batchSqlSession);
        }
        return true;
    }

    @Override
    public boolean update(T entity, Wrapper<T> wrapper) {
        return retBool(baseDao.update(entity, wrapper));
    }

    @Override
    public T get(String id) {
        return baseDao.get(id);
    }

    @Override
    public List<T> list(Wrapper<T> wrapper) {
        return baseDao.list(wrapper);
    }

    @Override
    public IPage<T> page(IPage<T> page, Wrapper<T> wrapper) {
        return baseDao.page(page, wrapper);
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
