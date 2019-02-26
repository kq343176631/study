package com.style.mybatis.plugin.dynamic;

import com.style.mybatis.annotation.DataSource;
import com.style.mybatis.exception.DatasourceException;
import com.style.mybatis.utils.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 数据源拦截器，在Executor构建后，动态切换数据源
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}

        )
})
public class DynamicDataSourceInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 接口的全限定名.方法命名 com.style.admin.modules.log.dao.logDao.insert
        String id = ((MappedStatement) invocation.getArgs()[0]).getId();
        String className = StringUtils.substringBeforeLast(id, ".");
        String methodName = StringUtils.substringAfterLast(id, ".");
        if (methodName.contains("!")) {
            methodName = StringUtils.substringBefore(methodName, "!");
        }
        // 获取MyBatis注解
        Method[] methods = Class.forName(className).getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }
        if (targetMethod == null) {
            throw new DatasourceException("获取DAO中的targetMethod失败。。。");

        }
        // 获取方法上的注解
        DataSource targetDataSource = targetMethod.getAnnotation(DataSource.class);
        if (targetDataSource != null && StringUtils.isNotBlank(targetDataSource.value())) {
            // 更新数据源
            DataSourceHolder.setDataSourceName(targetDataSource.value());
        } else {
            DataSourceHolder.setDataSourceName(null);
        }
        // 调用下一个拦截器
        return invocation.proceed();
    }

    /**
     * 对目标对象（需要拦截的对象）进行包装，生成代理对象，实现拦截功能。
     *
     * @param target target
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
