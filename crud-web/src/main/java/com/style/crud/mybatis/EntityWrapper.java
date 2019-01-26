package com.style.crud.mybatis;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class EntityWrapper<T> extends AbstractWrapper<T, String, EntityWrapper<T>> {

    /**
     * 查询字段
     */
    private String sqlSelect;

    /**
     * SQL 更新字段内容，例如：name='1',age=2
     */
    private final List<String> sqlSet;

    public EntityWrapper() {
        this(null);
    }

    public EntityWrapper(T entity) {
        this.setEntity(entity);
        this.sqlSet = new ArrayList<>();
        this.initNeed();
    }

    private EntityWrapper(T entity, Class<T> entityClass, List<String> sqlSet, AtomicInteger paramNameSeq,
                          Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments) {
        super.setEntity(entity);
        this.entityClass = entityClass;
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
    }

    public EntityWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect = String.join(StringPool.COMMA, columns);
        }
        return typedThis;
    }

    public EntityWrapper<T> select(Predicate<TableFieldInfo> predicate) {
        return select(entityClass, predicate);
    }

    /**
     * <p>
     * 过滤查询的字段信息(主键除外!)
     * </p>
     * <p>
     * 例1: 只要 java 字段名以 "test" 开头的              -> select(i -> i.getProperty().startsWith("test"))
     * 例2: 只要 java 字段属性是 CharSequence 类型的       -> select(TableFieldInfo::isCharSequence)
     * 例3: 只要 java 字段没有填充策略的                   -> select(i -> i.getFieldFill == FieldFill.DEFAULT)
     * 例4: 要全部字段                                   -> select(i -> true)
     * 例5: 只要主键字段                                 -> select(i -> false)
     * </p>
     *
     * @param predicate 过滤方式
     * @return this
     */
    public EntityWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.entityClass = entityClass;
        this.sqlSelect = TableInfoHelper.getTableInfo(getCheckEntityClass()).chooseSelect(predicate);
        return typedThis;
    }

    /**
     * <p>
     * SQL SET 字段
     * </p>
     *
     * @param column 字段
     * @param val    值
     */
    public EntityWrapper<T> set(String column, Object val) {
        return this.set(true, column, val);
    }

    /**
     * <p>
     * SQL SET 字段
     * </p>
     *
     * @param condition 操作条件
     * @param column    字段
     * @param val       值
     */
    public EntityWrapper<T> set(boolean condition, String column, Object val) {
        if (condition) {
            sqlSet.add(String.format("%s=%s", column, formatSql("{0}", val)));
        }
        return typedThis;
    }

    /**
     * <p>
     * SET 部分 SQL
     * </p>
     *
     * @param sql SET 部分内容
     */
    public EntityWrapper<T> setSql(String sql) {
        sqlSet.add(sql);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect;
    }

    @Override
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(StringPool.COMMA, sqlSet);
    }

    @Override
    protected String columnToString(String column) {
        return column;
    }

    @Override
    protected EntityWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new EntityWrapper<>(entity, entityClass, sqlSet, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
