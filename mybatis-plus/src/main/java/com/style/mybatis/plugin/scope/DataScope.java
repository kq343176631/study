package com.style.mybatis.plugin.scope;

/**
 * 数据范围
 */
public class DataScope {

    /**
     * 需要过滤的SQL
     */
    private String sqlFilter;

    public DataScope(String sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    @Override
    public String toString() {
        return this.sqlFilter;
    }

    public String getSqlFilter() {
        return sqlFilter;
    }

    public void setSqlFilter(String sqlFilter) {
        this.sqlFilter = sqlFilter;
    }
    
}