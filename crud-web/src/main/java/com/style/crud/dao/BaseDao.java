package com.style.crud.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.style.crud.entity.BaseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * BaseDao
 */
public interface BaseDao<T extends BaseEntity<T>> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    Integer insert(T entity);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     */
    Integer delete(@Param(Constants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    Integer deleteById(String id);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    Integer deleteByIds(@Param(Constants.COLLECTION) Collection<? extends String> idList);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity  实体对象 (set 条件值,不能为 null)
     * @param wrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    Integer update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    Integer updateById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T get(String id);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     */
    List<T> list(@Param(Constants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param page    分页查询条件（可以为 RowBounds.DEFAULT）
     * @param wrapper 实体对象封装操作类（可以为 null）
     */
    IPage<T> page(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> wrapper);

}