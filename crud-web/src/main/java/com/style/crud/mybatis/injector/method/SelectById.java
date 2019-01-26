package com.style.crud.mybatis.injector.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.AbstractLogicMethod;
import com.style.crud.mybatis.injector.SqlMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

/**
 * 根据 ID 查询
 */
public class SelectById extends AbstractLogicMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod;
        String sql;
        if (tableInfo.isLogicDelete()) {
            sqlMethod = SqlMethod.LOGIC_SELECT_BY_ID;
            sql = String.format(sqlMethod.getSql(),
                    sqlSelectColumns(tableInfo, false),
                    tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                    tableInfo.getLogicDeleteSql(true, false)
            );
        } else {
            sqlMethod = SqlMethod.SELECT_BY_ID;
            sql = String.format(sqlMethod.getSql(),
                    this.sqlSelectColumns(tableInfo, false),
                    tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty()
            );
        }
        SqlSource sqlSource = new RawSqlSource(configuration, sql, Object.class);
        return addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, tableInfo);
    }
}
