package com.style.mybatis.plus;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.style.mybatis.mapper.BaseMapper;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Mybatis 全局缓存
 * </p>
 *
 * @author Caratacus
 * @since 2016-12-06
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class GlobalConfig implements Serializable {

    /**
     * 是否开启 LOGO
     */
    private boolean banner = true;
    /**
     * 是否刷新 mapper
     * @deprecated since 2018-11-26
     */
    @Deprecated
    private boolean refresh = false;
    /**
     * 缓存 Sql 解析初始化
     */
    private boolean sqlParserCache = false;
    /**
     * 机器 ID 部分
     */
    private Long workerId;
    /**
     * 数据标识 ID 部分
     */
    private Long datacenterId;
    /**
     * 数据库相关配置
     */
    private DbConfig dbConfig;
    /**
     * SQL注入器
     */
    private ISqlInjector sqlInjector;
    /**
     * Mapper父类
     */
    private Class superMapperClass = BaseMapper.class;
    /**
     * 缓存当前Configuration的SqlSessionFactory
     */
    @Setter(value = AccessLevel.NONE)
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 缓存已注入CRUD的Mapper信息
     */
    private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<>();
    /**
     * 元对象字段填充控制器
     */
    private MetaObjectHandler metaObjectHandler;

    /**
     * <p>
     * 标记全局设置 (统一所有入口)
     * </p>
     */
    public void signGlobalConfig(SqlSessionFactory sqlSessionFactory) {
        if (null != sqlSessionFactory) {
            GlobalConfigUtils.setGlobalConfig(sqlSessionFactory.getConfiguration(), this);
        }
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Data
    public static class DbConfig {

        /**
         * 数据库类型
         */
        private DbType dbType = DbType.OTHER;
        /**
         * 主键类型（默认 ID_WORKER）
         */
        private IdType idType = IdType.ID_WORKER;
        /**
         * 表名前缀
         */
        private String tablePrefix;
        /**
         * 表名、是否使用下划线命名（默认 true:默认数据库表下划线命名）
         */
        private boolean tableUnderline = true;
        /**
         * String 类型字段 LIKE
         */
        private boolean columnLike = false;
        /**
         * 大写命名
         */
        private boolean capitalMode = false;
        /**
         * 表关键词 key 生成器
         */
        private IKeyGenerator keyGenerator;
        /**
         * 逻辑删除全局值（默认 1、表示已删除）
         */
        private String logicDeleteValue = "1";
        /**
         * 逻辑未删除全局值（默认 0、表示未删除）
         */
        private String logicNotDeleteValue = "0";
        /**
         * 字段验证策略
         */
        private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;
    }
}
