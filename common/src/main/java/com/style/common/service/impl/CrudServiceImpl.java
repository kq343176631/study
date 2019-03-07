package com.style.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.style.common.dao.BaseDao;
import com.style.common.model.Page;
import com.style.common.service.CrudService;
import com.style.common.utils.PageUtils;
import com.style.mybatis.injector.SqlMethod;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class CrudServiceImpl<D extends BaseDao<T>, T> extends BaseServiceImpl<T> implements CrudService<T> {

    @Autowired
    protected D baseDao;

    @Override
    public boolean save(T entity) {
        return retBool(baseDao.insert(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, 100);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
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
    public boolean deleteById(Long id) {
        return SqlHelper.delBool(baseDao.deleteById(id));
    }

    @Override
    public boolean deleteByIds(Collection<? extends Long> idList) {
        return SqlHelper.delBool(baseDao.deleteByIds(idList));
    }

    @Override
    public boolean delete(Wrapper<T> wrapper) {
        return SqlHelper.delBool(baseDao.delete(wrapper));
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
    public T get(Long id) {
        return baseDao.get(id);
    }

    @Override
    public List<T> list(Map<String, Object> params) {
        return baseDao.list(getWrapper(params));
    }

    @Override
    public Page<T> page(Map<String, Object> params) {
        return (Page<T>) baseDao.page(PageUtils.getPage(params), getWrapper(params));
    }

}
