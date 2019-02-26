package com.style.mybatis.plugin.scope;

/**
 * 数据范围
 */
public class DataScope {

    /**
     * 需要过滤的SQL
     */
    private String sql;

    private String filterSql;

    public DataScope(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return this.sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}