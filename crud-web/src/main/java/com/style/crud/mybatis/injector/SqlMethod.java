package com.style.crud.mybatis.injector;

/**
 * 支持的 CRUD 方法
 */
public enum SqlMethod {

    /**
     * 插入（通用）
     */
    INSERT("insert", "插入一条数据（选择字段插入）", "<script>\nINSERT INTO %s %s VALUES %s\n</script>"),

    /**
     * 删除
     */
    DELETE("delete", "根据 entity 条件删除记录", "<script>\nDELETE FROM %s %s\n</script>"),
    DELETE_BY_ID("deleteById", "根据ID 删除一条数据", "<script>\nDELETE FROM %s WHERE %s=#{%s}\n</script>"),
    DELETE_BY_IDS("deleteByIds", "根据ID集合，批量删除数据", "<script>\nDELETE FROM %s WHERE %s IN (%s)\n</script>"),

    /**
     * 逻辑删除
     */
    LOGIC_DELETE("delete", "根据 entity 条件逻辑删除记录", "<script>\nUPDATE %s %s %s\n</script>"),
    LOGIC_DELETE_BY_ID("deleteById", "根据ID 逻辑删除一条数据", "<script>\nUPDATE %s %s WHERE %s=#{%s} %s\n</script>"),
    LOGIC_DELETE_BY_IDS("deleteByIds", "根据ID集合，批量逻辑删除数据", "<script>\nUPDATE %s %s WHERE %s IN (%s) %s\n</script>"),

    /**
     * 修改（通用）
     */
    UPDATE("update", "根据 whereEntity 条件，更新记录", "<script>\nUPDATE %s %s %s\n</script>"),
    UPDATE_BY_ID("updateById", "根据ID 选择修改数据", "<script>\nUPDATE %s %s WHERE %s=#{%s} %s\n</script>"),

    /**
     * 查询
     */
    SELECT_LIST("list", "查询满足条件所有数据", "<script>\nSELECT %s FROM %s %s\n</script>"),
    SELECT_PAGE("page", "查询满足条件所有数据（并翻页）", "<script>\nSELECT %s FROM %s %s\n</script>"),
    SELECT_BY_ID("get", "根据ID 查询一条数据", "SELECT %s FROM %s WHERE %s=#{%s}"),

    /**
     * 逻辑删除 -> 查询
     */
    LOGIC_SELECT_BY_ID("get", "根据ID 查询一条数据", "SELECT %s FROM %s WHERE %s=#{%s} %s");


    private final String method;
    private final String desc;
    private final String sql;

    SqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }

}
