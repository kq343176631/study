package com.style.mybatis.injector.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.AbstractLogicMethod;
import com.style.mybatis.injector.SqlMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 根据 entity 有值字段 以及 updateWrapper 的条件更新
 */
public class Update extends AbstractLogicMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.UPDATE;
        String sql;
        if (tableInfo.isLogicDelete()) {
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                    sqlSet(true, true, tableInfo, ENTITY_DOT),
                    sqlWhereEntityWrapper(true, tableInfo));
        } else {
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                    sqlSet(false, true, tableInfo, ENTITY_DOT),
                    sqlWhereEntityWrapper(true, tableInfo));
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }
}
