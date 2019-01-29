package com.style.common.mybatis.injector.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.injector.AbstractLogicMethod;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.style.common.mybatis.injector.SqlMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 根据 ID 更新有值字段
 */
public class UpdateById extends AbstractLogicMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        boolean logicDelete = tableInfo.isLogicDelete();
        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        StringBuilder append = new StringBuilder("<if test=\"et instanceof java.util.Map\">")
                .append("<if test=\"et.").append(OptimisticLockerInterceptor.MP_OPTLOCK_VERSION_ORIGINAL).append("!=null\">")
                .append(" AND ${et.").append(OptimisticLockerInterceptor.MP_OPTLOCK_VERSION_COLUMN)
                .append("}=#{et.").append(OptimisticLockerInterceptor.MP_OPTLOCK_VERSION_ORIGINAL).append(StringPool.RIGHT_BRACE)
                .append("</if></if>");
        if (logicDelete) {
            append.append(tableInfo.getLogicDeleteSql(true, false));
        }
        sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                sqlSet(logicDelete, false, tableInfo, Constants.ENTITY_DOT),
                tableInfo.getKeyColumn(), Constants.ENTITY_DOT + tableInfo.getKeyProperty(),
                append);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }
}
