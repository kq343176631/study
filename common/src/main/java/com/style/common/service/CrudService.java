package com.style.common.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.style.common.model.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CrudService<T> extends BaseService<T> {

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     */
    boolean save(T entity);

    /**
     * 插入（批量），该方法不支持 Oracle、SQL Server
     *
     * @param entityList 实体对象集合
     */
    boolean saveBatch(Collection<T> entityList);

    /**
     * 插入（批量），该方法不支持 Oracle、SQL Server
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    boolean saveBatch(Collection<T> entityList, int batchSize);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    boolean deleteById(Long id);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表
     */
    boolean deleteByIds(Collection<? extends Long> idList);

    /**
     * 根据 wrapper 删除
     *
     * @param wrapper wrapper
     */
    boolean delete(Wrapper<T> wrapper);

    boolean updateById(T entity);

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     */
    boolean updateBatchById(Collection<T> entityList);

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    boolean updateBatchById(Collection<T> entityList, int batchSize);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity  实体对象
     * @param wrapper 实体对象封装操作类
     */
    boolean update(T entity, Wrapper<T> wrapper);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T get(Long id);

    /**
     * 根据 wrapper 查询
     *
     * @param params params
     */
    List<T> list(Map<String,Object> params);

    /**
     * 根据 wrapper 分页查询
     *
     * @param params params
     */
    Page<T> page(Map<String, Object> params);
}
